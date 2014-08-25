package com.lombardrisk.xbrl.render.csv;

import com.google.common.collect.Lists;
import com.lombardrisk.xbrl.checker.csv.CsvUtil;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.*;

public class TableCodeOpenCodeEntryDescriptorTest {
    @Test
    public void testExport() throws Exception {
        OpenCodeEntry o = new OpenCodeEntry("Dim", 1);
        TableCodeOpenCodeEntry t = new TableCodeOpenCodeEntry();
        t.setTableCode("C 09.00");
        t.setOpenCodeEntry(o);
        StringWriter writer = new StringWriter();
        CsvUtil.write(writer, new TableCodeOpenCodeEntryDescriptor(), Lists.newArrayList(t), true);
        String export = writer.toString();

        List<TableCodeOpenCodeEntry> result = CsvUtil.parse(new StringReader(export), new TableCodeOpenCodeEntryDescriptor(), true);
        System.out.println(result.get(0).getOpenCodeEntry().getDimensionCode());
    }
}