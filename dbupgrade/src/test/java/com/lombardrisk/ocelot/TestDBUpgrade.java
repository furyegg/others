package com.lombardrisk.ocelot;

import org.junit.Before;
import org.junit.Test;

import com.lombardirks.ocelot.DBUpgrade;

public class TestDBUpgrade {

	private DBUpgrade upgrade;
	private String topVersion;

	@Before
	public void init() throws Exception {
		upgrade = new DBUpgrade("sqlserver");
	}

	@Test
	public void testFindTopVersion() throws Exception {
		String ver = upgrade.findTopVersion();
		System.out.println(ver);
	}

	@Test
	public void testExecuteUpgrade() throws Exception {
		upgrade.executeUpgrade(topVersion);
	}

}
