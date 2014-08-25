package com.lombardrisk.xbrl.checker.controller;

import org.junit.Test;

import javax.ws.rs.core.UriBuilder;

import static org.junit.Assert.*;

public class ValidationIdValidatorTest {

    @org.junit.Test
    public void testGetValidationId() throws Exception {
        assertEquals("v1502_m", ValidationIdValidator.getValidationId("eba_v1502_m"));
        assertEquals("v1502_m", ValidationIdValidator.getValidationId("v1502_m"));
        assertEquals(null, ValidationIdValidator.getValidationId("adsd"));
        assertEquals(null, ValidationIdValidator.getValidationId(null));
    }

    @Test
    public void testUri() throws Exception {

//        UriBuilder.fromPath("localHost").host("localhost").queryParam();

    }
}