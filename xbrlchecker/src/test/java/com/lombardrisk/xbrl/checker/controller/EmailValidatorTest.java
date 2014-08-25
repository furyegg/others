package com.lombardrisk.xbrl.checker.controller;

import org.junit.Test;

import static org.junit.Assert.*;

public class EmailValidatorTest {

    @Test
    public void testIsEmailValid() throws Exception {
        assertTrue(EmailValidator.isEmailValid("cesar.tl@lrm.com"));
        assertTrue(EmailValidator.isEmailValid("cesar.t'l@lrm.com"));
        assertTrue(EmailValidator.isEmailValid("cesar.t'l@lrm.c"));
        assertFalse(EmailValidator.isEmailValid("cesar.t'l@.c"));
    }
}