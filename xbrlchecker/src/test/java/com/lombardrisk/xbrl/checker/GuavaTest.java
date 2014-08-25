package com.lombardrisk.xbrl.checker;

import com.google.common.util.concurrent.AtomicLongMap;
import org.junit.Test;

import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;

/**
 * Created by Cesar on 21/05/2014.
 */
public class GuavaTest {

    @Test
    public void testAtomicLongMap() throws Exception {
        AtomicLongMap<String> map = AtomicLongMap.create();
        assertEquals(0, map.get("a"));
        assertEquals(0, map.get("b"));

        assertEquals(1, map.incrementAndGet("a"));
        assertEquals(1, map.get("a"));

        assertEquals(1, map.incrementAndGet("b"));
        assertEquals(1, map.get("b"));
    }

    @Test
    public void testSecureRandom() throws Exception {
        SecureRandom instance = SecureRandom.getInstance("SHA1PRNG", "SUN");
        instance.nextBytes(new byte[256]);
        long l = instance.nextLong();
        System.out.println(Long.toHexString(l));
    }
}
