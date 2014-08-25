package com.lombardirks.ocelot;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import com.lombardirks.ocelot.dao.UpgradeDao;
import com.lombardirks.ocelot.exception.DBUException;
import com.lombardirks.ocelot.model.Batch;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class SQLFileHelper {

	private BatchBuilder batchBuilder;

	private String dbType;
	private String dbClassPath;
	private String oldLatestVersion;
	private List<String> versionFileList;

	private UpgradeDao upgradeDao;

	/**
	 * initialize
	 */
	public void init(String databaseType) throws Exception {
		dbType = databaseType.toLowerCase();
		dbClassPath = getDbClassPath();
		versionFileList = findAllVersionConfXML(getDbType());
	}

	private String getDbClassPath() {
		StringBuilder path = new StringBuilder(Constants.DB_DIR).append("/");
		path.append(getDbType()).append("/");
		return path.toString();
	}

	private List<String> findAllVersionConfXML(String dbType) throws Exception {
		StringBuilder loc = new StringBuilder("classpath:").append(dbClassPath).append(Constants.XML_PREFIX).append("*.xml");

		ResourcePatternResolver resovler = new PathMatchingResourcePatternResolver();
		Resource[] ress = resovler.getResources(loc.toString());
		List<String> list = new ArrayList<String>(ress.length);
		for (int i = 0; i < ress.length; i++) {
			Resource resource = ress[i];
			list.add(resource.getFilename());
		}

		// sort version file name ascending
		Collections.sort(list);
		return list;
	}

	/**
	 * get SQL statement from a file.
	 * 
	 * @param name
	 *            SQL file name. Note: This file can contain only one statement.
	 * @return SQL statement string.
	 */
	public String getSQL(String fileName) throws DBUException {
		StringBuilder file = new StringBuilder(Constants.DB_DIR).append("/");
		file.append(getDbType()).append("/").append(fileName);
		ClassPathResource res = new ClassPathResource(file.toString());

		Scanner in = null;
		try {
			in = new Scanner(res.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new DBUException("The specific file: " + file + " doesn't existed.");
		}

		StringBuilder sql = new StringBuilder();
		while (in.hasNextLine()) {
			sql.append(in.nextLine()).append(" ");
		}

		in.close();

		return eliminateLastColon(sql.toString());
	}

	/**
	 * Get SQL statement from a file, and replace quote of parameters.
	 * 
	 * @param fileName
	 *            SQL file name. Note: This file can contain only one statement.
	 * @param params
	 *            parameters map, the key must same as quotes in statement.
	 * @return SQL statement string.
	 */
	public String getSQL(String fileName, Map<String, Object> params) throws DBUException {
		String sql = getSQL(fileName);
		return fillParameters(sql, params);
	}

	private String fillParameters(String sql, Map<String, Object> params) throws DBUException {
		Configuration templateConfig = new Configuration();
		templateConfig.setObjectWrapper(new DefaultObjectWrapper());

		Template template = null;
		StringWriter out = new StringWriter();
		try {
			template = new Template("sql", new StringReader(sql), templateConfig);
			template.process(params, out);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBUException("Process SQL error: " + sql + ", params: " + params.toString() + ", database type: " + getDbType());
		}

		return out.toString();
	}

	/**
	 * Get version file name which begin version belongs to.
	 * 
	 * @param ver
	 *            e.g. 1.2.3
	 * @return version file name, e.g. ver_1.2.xml
	 */
	public String getVersionFile(String ver) {
		if (!StringUtils.hasText(ver)) {
			return null;
		}

		String mainVer = getReleaseVersion(ver);
		StringBuilder file = new StringBuilder(Constants.XML_PREFIX).append(mainVer).append(".xml");
		return file.toString();
	}

	/**
	 * Get version.
	 * 
	 * @param xmlFileName
	 *            xml file name format e.g.: ver_1.2.3.xml.
	 * @return version number, such as get 1.2.3 from ver_1.2.3.xml
	 */
	public String getVersion(String xmlFileName) {
		String fileName = xmlFileName.toLowerCase();
		return fileName.substring(Constants.XML_PREFIX.length(), fileName.indexOf(".xml"));
	}

	/**
	 * get release version, release version = first + second version number.
	 * 
	 * @param versionNumber
	 *            e.g. 1.2.3 or 1.2.3.4
	 * @return first and second version number, such as get 1.2 from 1.2.3 or 1.2.3.4
	 */
	public String getReleaseVersion(String versionNumber) {
		int firstDot = versionNumber.indexOf(".");
		int secondDot = versionNumber.substring(firstDot + 1).indexOf(".") + firstDot + 1;
		return versionNumber.substring(0, secondDot);
	}

	/**
	 * upgrade to latest version in version file.
	 * 
	 * @param versionFile
	 *            specific version file
	 * @return the latest version upgrade to
	 */
	public String upgrade(String versionFile) throws Exception {
		Map<String, Batch> batchMap;
		try {
			ClassPathResource res = new ClassPathResource(dbClassPath + versionFile);
			batchMap = batchBuilder.build(res.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBUException("Failed to parse file: " + versionFile);
		}

		List<String> verList = new ArrayList<String>(batchMap.keySet());
		String startVersion = getStartVersion(verList, oldLatestVersion);
		String latestVer = startVersion;

		if (startVersion != null) {
			for (String ver : batchMap.keySet()) {
				if (startVersion.compareTo(ver) < 1) {
					System.out.print("Starting upgrade to: " + ver + " ... ");

					Batch batch = batchMap.get(ver);
					if (StringUtils.hasText(batch.getFile())) {
						ClassPathResource res = new ClassPathResource(dbClassPath + batch.getFile());
						upgradeDao.executeSQLFile(res.getFile());
					} else {
						for (String sql : batch.getSqlList()) {
							upgradeDao.executeSQL(eliminateLastColon(sql));
						}
					}
					latestVer = ver;

					System.out.println("Upgrade successfully!");
				}
			}
		}

		return latestVer;
	}

	/**
	 * Get start version in current version file,<br/>
	 * if begin version does't belong to current version file, return first version in file.<br/>
	 * 
	 * @return start version in version file. null, if the version is the latest version already.
	 */
	private String getStartVersion(List<String> verList, String version) throws Exception {
		if (StringUtils.hasText(version) && verList.contains(version)) {
			int index = verList.indexOf(version);
			if (index == verList.size() - 1) {
				return null;
			}

			return verList.get(verList.indexOf(version) + 1);

		} else {
			return verList.get(0);
		}
	}

	private String eliminateLastColon(String sql) {
		sql = sql.trim();
		if (org.apache.commons.lang.StringUtils.endsWith(sql, ";")) {
			return sql.substring(0, sql.length() - 1);
		}
		return sql;
	}

	// --------------------------------------------------------------------------------
	//
	// getter and setter
	//
	// --------------------------------------------------------------------------------

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getOldLatestVersion() {
		return oldLatestVersion;
	}

	public void setOldLatestVersion(String oldLatestVersion) {
		this.oldLatestVersion = oldLatestVersion;
	}

	public List<String> getVersionFileList() {
		return versionFileList;
	}

	public void setVersionFileList(List<String> versionFileList) {
		this.versionFileList = versionFileList;
	}

	public BatchBuilder getBatchBuilder() {
		return batchBuilder;
	}

	public void setBatchBuilder(BatchBuilder batchBuilder) {
		this.batchBuilder = batchBuilder;
	}

	public UpgradeDao getUpgradeDao() {
		return upgradeDao;
	}

	public void setUpgradeDao(UpgradeDao upgradeDao) {
		this.upgradeDao = upgradeDao;
	}

}
