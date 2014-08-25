package com.lombardrisk.ocelot;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.lombardirks.ocelot.BatchBuilder;
import com.lombardirks.ocelot.XMLBatchBuilder;

public class TestXMLBatchBuilder {

	private String file;
	private String path;
	private BatchBuilder builder;

	@Before
	public void init() {
		path = "classpath:db/sqlserver/";
		file = "ver_1.1.xml";
		builder = new XMLBatchBuilder();
	}

	@Test
	public void testBuild() throws Exception {
		InputStream in = new FileInputStream("src/main/resources/db/sqlserver/" + file);
		try {
			builder.build(in);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

}
