package com.lombardrisk.xbrl.render.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileUtilsTest {

    @Test
    public void testGetBaseFileName() throws Exception {
        String s = "foo.xbrl.gz";
        String name = FileUtils.getBaseFileName(s);
        assertEquals("foo", name);
    }
}