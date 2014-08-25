package com.lombardrisk.xbrl.checker.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EmailTemplateUtilTest {

    @Test
    public void testLoadEmail() throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("[name]", "foo");
        String content = EmailTemplateUtil.loadEmail("testTemplate.html", param);
        assertEquals("Hello my name is foo because they call me foo", content);
    }
}