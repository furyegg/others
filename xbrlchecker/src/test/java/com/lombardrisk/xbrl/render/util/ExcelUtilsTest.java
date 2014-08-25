package com.lombardrisk.xbrl.render.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

public class ExcelUtilsTest {

    @Ignore
    @Test
    public void testCloneSheet() throws Exception {
        Workbook wb = getWorkbook("testClone.xlsx");
        ExcelUtils.cloneSheet(wb, 1, Lists.newArrayList("2-a", "2-b"), true);
        FileOutputStream fw = new FileOutputStream(new File("testClone-out.xlsx"));
        wb.write(fw);
    }

    @Ignore
    @Test
    public void testCloneSheets() throws Exception {
        Workbook wb = getWorkbook("testClone.xlsx");
        SetMultimap<String, String> names = HashMultimap.create();
        names.put("Sheet1", "1-a");
        names.put("Sheet1", "1-b");
        names.put("Sheet2", "2-a");
        names.put("Sheet2", "2-b");
        names.put("Sheet3", "3-a");
        names.put("Sheet3", "3-b");
        ExcelUtils.cloneSheets(wb, names, true);
        FileOutputStream fw = new FileOutputStream(new File("testClone-out.xlsx"));
        wb.write(fw);
    }

    private Workbook getWorkbook(String name) throws IOException, InvalidFormatException {
        return WorkbookFactory.create(this.getClass().getClassLoader().getResourceAsStream(name));
    }
}