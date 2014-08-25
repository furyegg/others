package com.lombardrisk.xbrl.render.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UUIDUtilsTest {

    @Test
    public void testSmallHexUUID() throws Exception {
        System.out.println("smallHexUUID: "+UUIDUtils.smallHexUUID());
    }

    @Test
    public void testSmallHexUUIDHumanReadable() throws Exception {
        System.out.println("smallHexUUIDHumanReadable: "+UUIDUtils.smallHexUUIDHumanReadable());
    }

    @Test
    public void testHumanReadableUUID() throws Exception {
        String token = UUIDUtils.smallHexUUID();
        System.out.println(token);
        String humanReadableToken = UUIDUtils.humanReadableUUID(token);
        System.out.println(humanReadableToken);
        String token2 = UUIDUtils.parseHumanReadableUUID(humanReadableToken);
        assertEquals(token, token2);
        assertEquals(token, UUIDUtils.parseHumanReadableUUID(token));
    }

}