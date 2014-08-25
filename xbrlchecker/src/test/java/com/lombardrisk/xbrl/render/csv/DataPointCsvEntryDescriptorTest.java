package com.lombardrisk.xbrl.render.csv;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.lombardrisk.xbrl.checker.csv.CsvUtil;
import com.lombardrisk.xbrl.checker.csv.DeactivatedValidationCsvDescriptor;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DataPointCsvEntryDescriptorTest {

    @Test
    public void testExport() throws Exception {
        DataPointCsvEntry e1 = new DataPointCsvEntry();
        e1.setKey("key1");
        e1.getElements().add("e1");
        e1.getElements().add("e2");
        e1.getElements().add("e3");

        DataPointCsvEntry e2 = new DataPointCsvEntry();
        e2.setKey("key2");
        e2.getElements().add("a");
        e2.getElements().add("b");
        e2.getElements().add("c");

        StringWriter writer = new StringWriter();

        CsvUtil.write(writer, new DataPointCsvEntryDescriptor(), Lists.newArrayList(e1, e2), true);
        String s = writer.toString();
        System.out.println(s);
        List<DataPointCsvEntry> result = CsvUtil.parse(new StringReader(s), new DataPointCsvEntryDescriptor(), true);
        System.out.println(Joiner.on("\n").join(result));
    }
}