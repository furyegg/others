package com.lombardirks.ocelot.model;

import java.util.List;

/**
 * Only one of external file and sqlList can be used in the same version.
 * 
 * @author Kyle Li
 */
public class Batch {

	private String version;

	/**
	 * External SQL file.<br/>
	 * If SQLs are too much to put into a XML file, put these SQLs into a external file.<br/>
	 * Note: If using external file, don't put SQL into XML file in the same version.
	 */
	private String file;

	/**
	 * SQL list.<br/>
	 * If putting SQL into XML file, don't specify external SQL file.
	 */
	private List<String> sqlList;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public List<String> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<String> sqlList) {
		this.sqlList = sqlList;
	}

}
