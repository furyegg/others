package com.lombardrisk.xbrl.checker.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TimeUtilsTest {

    @Test
    public void testGetTimeDifferenceInSeconds() throws Exception {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = (Calendar) c1.clone();

        c2.add(Calendar.SECOND, 15);

        assertEquals(15, TimeUtils.getTimeDifferenceInSeconds(c2, c1, TimeUnit.SECONDS));
    }
}