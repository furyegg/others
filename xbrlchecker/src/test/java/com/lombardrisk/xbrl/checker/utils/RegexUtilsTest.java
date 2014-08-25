package com.lombardrisk.xbrl.checker.utils;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RegexUtilsTest {

    @Test
    public void testGetTableCodes() throws Exception {
        String s = "sdlskd C 19.29.d sdsdsd F 00.12";
        List<String> tableCodes = RegexUtils.getTableCodes(s);
        System.out.println(tableCodes.size());
        assertEquals("C 19.29.d", tableCodes.get(0));
        assertEquals("F 00.12", tableCodes.get(1));
    }
}