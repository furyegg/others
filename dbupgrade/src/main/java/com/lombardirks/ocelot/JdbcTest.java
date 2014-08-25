package com.lombardirks.ocelot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.SQLExec;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class JdbcTest {

	private static JdbcTemplate jdbcTemplate;

	public static void main(String[] args) throws Exception {
		ClassPathResource res = new ClassPathResource("db/sqlserver/ver_1.1.1.sql");

		String sql = "select * \n from table;";
		System.out.println(eliminateLastColon(sql));
	}

	private static String eliminateLastColon(String sql) {
		if (org.apache.commons.lang.StringUtils.endsWith(sql, ";")) {
			return sql.substring(0, sql.length() - 1);
		}
		return sql;
	}

	private static void batchUpdate(List<String> sqlList, int batch) throws Exception {
		List<String> sqls = new LinkedList<String>();
		int index = 0;
		int i = 0;

		while (index < sqlList.size()) {
			if (i == batch) {
				System.out.println("execute a batch1");

				i = 0;
				sqls = new LinkedList<String>();
			}

			sqls.add(sqlList.get(index));

			if (index == sqlList.size() - 1) {
				System.out.println("execute a batch2");
			}

			++index;
			++i;
		}
	}

	private static SQLExec getSQLExec() {
		BeanFactory factory = new ClassPathXmlApplicationContext("classpath:" + Constants.CONTEXT_XML);
		return (SQLExec) factory.getBean("sqlExec");
	}

	private static void transTest() {
		BeanFactory factory = new ClassPathXmlApplicationContext("classpath:" + Constants.CONTEXT_XML);
		jdbcTemplate = (JdbcTemplate) factory.getBean("jdbcTemplate");

		DataSourceTransactionManager m = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
		TransactionDefinition td = new DefaultTransactionDefinition();
		TransactionStatus ts = m.getTransaction(td);

		try {
			jdbcTemplate.execute("insert into upgrade_test values ('1')");
			jdbcTemplate.execute("insert into upgrade_test values ('2')");
			jdbcTemplate.execute("insert into upgrade_test values ('3')");
			m.commit(ts);

		} catch (Exception e) {
			e.printStackTrace();
			m.rollback(ts);
		}
	}

	private static Connection getConnection() throws Exception {
		Properties p = new Properties();
		ClassPathResource res = new ClassPathResource("jdbc.properties");
		p.load(res.getInputStream());

		String driver = p.getProperty("jdbc.driver");
		System.setProperty("jdbc.drivers", driver);

		String url = p.getProperty("jdbc.url");
		String user = p.getProperty("jdbc.user");
		String password = p.getProperty("jdbc.password");

		return DriverManager.getConnection(url, user, password);

		// Connection conn = getConnection();
		// Statement statement = conn.createStatement();
		// statement
		// .execute("insert into upgrade_test values ('1.1.1.1') insert into upgrade_test values ('1.1.1.2') insert into upgrade_test values ('1.1.1.3')");
		// conn.close();
	}

}
