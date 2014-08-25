package com.lombardirks.ocelot;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

import com.lombardirks.ocelot.dao.UpgradeDao;
import com.lombardirks.ocelot.exception.DBUException;

public class DBUpgrade {

	private SQLFileHelper helper;
	private UpgradeDao upgradeDao;
	private JdbcTemplate jdbcTemplate;
	private DataSourceTransactionManager txManager;

	public static void main(String[] args) {

		DBUpgrade up = null;
		try {
			up = new DBUpgrade(getDBType(args));
			up.upgrade();
		} catch (DBUException e) {
			System.out.println("Fatal Error: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getDBType(String[] args) throws DBUException {
		if (args.length == 0) {
			throw new DBUException("Please pass the database type parameter to DBUpgrade");
		}

		String type = args[0];
		if (Constants.SUPPORTED_DBTYPES.indexOf(type) < 0) {
			throw new DBUException("Supported database types: oracle, sqlserver. The " + type + " doesn't supported.");
		}

		return type;
	}

	public DBUpgrade(String databaseType) throws Exception {
		BeanFactory factory = new ClassPathXmlApplicationContext("classpath:" + Constants.CONTEXT_XML);
		upgradeDao = (UpgradeDao) factory.getBean("upgradeDao");
		jdbcTemplate = (JdbcTemplate) factory.getBean("jdbcTemplate");
		txManager = (DataSourceTransactionManager) factory.getBean("txManager");
		helper = (SQLFileHelper) factory.getBean("sqlFileHelper");
		helper.init(databaseType);
	}

	public void upgrade() throws Exception {
		String topVer = null;
		String latestVer = null;
		TransactionStatus ts = null;

		try {
			// transaction begin
			TransactionDefinition td = new DefaultTransactionDefinition();
			ts = txManager.getTransaction(td);

			topVer = findTopVersion();

			// upgrade
			latestVer = executeUpgrade(topVer);

			// transaction end
			txManager.commit(ts);

		} catch (Exception e) {
			txManager.rollback(ts);
			System.out.println("All modifications have been rollback.");
			throw e;
		}

		if (latestVer == null) {
			System.out.println("The current version: " + topVer + " is the latest version.");
		} else {
			String oldVer = StringUtils.hasText(topVer) ? topVer : "initial";
			System.out.println("Upgrade version from: " + oldVer + " to: " + latestVer + " successfully!");
		}
	}

	// --------------------------------------------------------------------------------
	//
	// private methods
	// Private method wouldn't be called by other class, it's for unit test.
	//
	// --------------------------------------------------------------------------------

	/**
	 * @private
	 * 
	 * @return if cann't find the top version, return null.
	 */
	public String findTopVersion() throws Exception {
		String ver = null;
		try {
			ver = upgradeDao.findTopVersion();
		} catch (DataAccessException e) {
			System.out.println("Create upgrade history table...");
			upgradeDao.createHistoryTable();
		}

		return ver;
	}

	/**
	 * @private
	 */
	public String executeUpgrade(String topVer) throws Exception {
		helper.setOldLatestVersion(topVer);

		// get top version file. if version is empty, get first file.
		String topVerFile = null;
		List<String> fileList = helper.getVersionFileList();
		if (StringUtils.hasText(topVer)) {
			topVerFile = helper.getVersionFile(topVer);
			if (Collections.binarySearch(fileList, topVerFile) < 0) {
				throw new DBUException("The version file in database: " + topVerFile + " doesn't existed in pre-defined version file list: "
						+ fileList.toString());
			}
		} else {
			topVerFile = fileList.get(0);
		}

		// upgrade
		String latestVer = null;
		for (String verFile : fileList) {
			if (StringUtils.hasText(topVerFile) && topVerFile.compareToIgnoreCase(verFile) > 0) {
				continue;
			}
			latestVer = helper.upgrade(verFile);
		}

		if (StringUtils.hasText(latestVer)) {
			// update latest version
			try {
				upgradeDao.updateTopVersion(latestVer, new Date());
			} catch (Exception e) {
				e.printStackTrace();
				throw new DBUException("Failed to update latest version.");
			}
		}

		return latestVer;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
