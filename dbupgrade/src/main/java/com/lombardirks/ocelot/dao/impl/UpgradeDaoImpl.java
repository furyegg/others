package com.lombardirks.ocelot.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.util.DateUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.lombardirks.ocelot.Constants;
import com.lombardirks.ocelot.SQLFileHelper;
import com.lombardirks.ocelot.dao.UpgradeDao;
import com.lombardirks.ocelot.util.ant.SQLExtractor;

public class UpgradeDaoImpl implements UpgradeDao {

	private JdbcTemplate jdbcTemplate;
	private SQLFileHelper sqlFileHelper;
	private int batchUpdateCount;

	@Override
	public void createHistoryTable() throws Exception {
		String sql = sqlFileHelper.getSQL(Constants.SQL_CREATE_TABLE);
		jdbcTemplate.execute(sql);
	}

	@Override
	public String findTopVersion() throws Exception {
		String sql = sqlFileHelper.getSQL(Constants.SQL_FIND_TOPVER);
		List<String> list = jdbcTemplate.queryForList(sql, String.class);

		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public void updateTopVersion(String ver, Date date) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("ver", ver);
		params.put("date", DateUtils.format(date, Constants.DATE_FORMAT));

		String sql = sqlFileHelper.getSQL(Constants.SQL_UPDATE_VER, params);
		jdbcTemplate.execute(sql);
	}

	@Override
	public void executeSQL(String sql) throws Exception {
		jdbcTemplate.execute(sql);
	}

	@Override
	public void executeSQLFile(File sqlFile) throws Exception {
		SQLExtractor ext = SQLExtractor.getInstance();
		ext.setKeepformat(false);
		InputStream is = new FileInputStream(sqlFile);
		List<String> sqlList = ext.extract(is);
		is.close();

		batchUpdate(sqlList, batchUpdateCount);
	}

	/**
	 * batch execute sql
	 * 
	 * @param sqlList sql statement list
	 * @param batch sql count in each batch
	 */
	private void batchUpdate(List<String> sqlList, int batch) throws Exception {
		List<String> sqls = new LinkedList<String>();
		int index = 0;
		int i = 0;

		while (index < sqlList.size()) {
			if (i == batch) {
				jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));

				i = 0;
				sqls = new LinkedList<String>();
			}

			sqls.add(sqlList.get(index));

			if (index == sqlList.size() - 1) {
				jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));
			}

			++index;
			++i;
		}
	}

	// --------------------------------------------------------------------------------
	//
	// getter and setter
	//
	// --------------------------------------------------------------------------------

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public SQLFileHelper getSqlFileHelper() {
		return sqlFileHelper;
	}

	public void setSqlFileHelper(SQLFileHelper sqlFileHelper) {
		this.sqlFileHelper = sqlFileHelper;
	}

	public int getBatchUpdateCount() {
		return batchUpdateCount;
	}

	public void setBatchUpdateCount(int batchUpdateCount) {
		this.batchUpdateCount = batchUpdateCount;
	}

}
