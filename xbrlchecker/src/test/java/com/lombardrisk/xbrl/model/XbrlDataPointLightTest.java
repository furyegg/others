package com.lombardrisk.xbrl.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class XbrlDataPointLightTest {

    @Test
    public void testOrdering() throws Exception {
        XbrlDataPointLight d1 = new XbrlDataPointLight(1, "a");
        XbrlDataPointLight d2 = new XbrlDataPointLight(1, "b");
        XbrlDataPointLight d3 = new XbrlDataPointLight(2, "a");
        XbrlDataPointLight d4 = new XbrlDataPointLight(2, "b");
        XbrlDataPointLight d5 = new XbrlDataPointLight(1, null);
        XbrlDataPointLight d6 = new XbrlDataPointLight(2, null);

        List<XbrlDataPointLight> list = Lists.newArrayList(d3, d2, d1, d4, d5, d6);
        List<XbrlDataPointLight> sorted = XbrlDataPointLight.getNaturalOrdering().sortedCopy(list);
        System.out.println(Joiner.on("\n").join(sorted));
    }
}