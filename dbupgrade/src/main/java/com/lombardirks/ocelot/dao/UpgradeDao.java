package com.lombardirks.ocelot.dao;

import java.io.File;
import java.util.Date;

public interface UpgradeDao {

	void createHistoryTable() throws Exception;

	/**
	 * the latest version in database
	 * 
	 * @return the latest version in database
	 */
	String findTopVersion() throws Exception;

	void updateTopVersion(String ver, Date date) throws Exception;

	/**
	 * Execute a single SQL statement
	 */
	void executeSQL(String sql) throws Exception;

	/**
	 * Execute all SQL statements in a SQL file
	 */
	void executeSQLFile(File sqlFile) throws Exception;

}
