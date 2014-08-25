package com.lombardirks.ocelot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.util.StringUtils;

import com.lombardirks.ocelot.exception.DBUException;
import com.lombardirks.ocelot.model.Batch;

@SuppressWarnings("unchecked")
public class XMLBatchBuilder implements BatchBuilder {

	private Map<String, Batch> map;

	@Override
	public Map<String, Batch> build(InputStream in) throws Exception {
		map = new TreeMap<String, Batch>();

		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(in);

		List<Element> batchElms = doc.getRootElement().getChildren();
		for (Element be : batchElms) {
			String ver = be.getAttribute(Constants.ELM_ATTRI_VERSION).getValue().trim();

			Batch batch = new Batch();
			Attribute fileAttr = be.getAttribute(Constants.ELM_ATTRI_FILE);
			batch.setFile(fileAttr == null ? null : fileAttr.getValue().trim());

			List<Element> sqlElms = be.getChildren();
			List<String> sqlList = new ArrayList<String>(sqlElms.size());
			if (!sqlElms.isEmpty()) {
				for (Element se : sqlElms) {
					sqlList.add(se.getText().trim());
				}
			}
			batch.setSqlList(sqlList);

			// validate SQL setting, only one of file and SQL list can be set in the same version.
			// file and SQL list can't be both null
			if (!StringUtils.hasText(batch.getFile()) && batch.getSqlList().isEmpty()) {
				throw new DBUException("Version: " + ver + " is empty (doesn't contain any SQL).");
			}

			// file and SQL list can't be both not null
			if (StringUtils.hasText(batch.getFile()) && !batch.getSqlList().isEmpty()) {
				throw new DBUException("Can't set external SQL file and SQL list in the same time in version:" + ver + ".");
			}

			map.put(ver, batch);
		}

		return map;
	}

	public Map<String, Batch> getMap() {
		return map;
	}

	public void setMap(Map<String, Batch> map) {
		this.map = map;
	}

}