package com.lombardrisk.xbrl.render.xml;

import com.lombardrisk.xbrl.render.util.ZipUtils;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class XbrlUtilsTest {

    @Test
    public void testGetEntranceSchema() throws Exception {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("xbrlSample.xml");
        String entranceSchema = XbrlUtils.getEntranceSchema(in);
        System.out.println(entranceSchema);
    }
}