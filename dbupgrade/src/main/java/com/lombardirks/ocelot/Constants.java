package com.lombardirks.ocelot;

public class Constants {

	public static final String CONTEXT_XML = "appContext.xml";
	public static final String HISTORY_TABLE = "UPGRADE_HISTORY";
	public static final String DB_DIR = "db";
	public static final String SUPPORTED_DBTYPES = "oracle,ORACLE,sqlserver,SQLSERVER";
	public static final String XML_PREFIX = "ver_";
	public static final String XML_SUFFIX = ".xml";

	public static final String ELM_ATTRI_VERSION = "ver";
	public static final String ELM_ATTRI_FILE = "file";
	public static final String ELM_NAME_SQL = "sql";

	public static final String SQL_UPDATE_VER = "updateLatestVersion.sql";
	public static final String SQL_CREATE_TABLE = "createHistoryTable.sql";
	public static final String SQL_FIND_TOPVER = "findLatestVersion.sql";

	public static final String DATE_FORMAT = "yyyy-MM-dd";

}
