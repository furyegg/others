package com.lombardrisk.ocelot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.lombardirks.ocelot.BatchBuilder;
import com.lombardirks.ocelot.SQLFileHelper;
import com.lombardirks.ocelot.XMLBatchBuilder;
import com.lombardirks.ocelot.model.Batch;

public class TestSQLFileHelper {

	private BatchBuilder batchBuilder;
	private SQLFileHelper helper;
	private String oldLatestVersion;

	@Before
	public void init() throws Exception {
		oldLatestVersion = "1.1.1";

		String file = "ver_1.1.xml";
		batchBuilder = new XMLBatchBuilder();

		helper = new SQLFileHelper();
		helper.setBatchBuilder(batchBuilder);
		helper.setOldLatestVersion(oldLatestVersion);
		helper.init("sqlserver");
	}

	@Test
	public void testUpgrade() throws Exception {
		String ver = helper.upgrade(helper.getVersionFile(oldLatestVersion));
		Assert.assertTrue(ver.equals("1.3.3"));
	}

}
