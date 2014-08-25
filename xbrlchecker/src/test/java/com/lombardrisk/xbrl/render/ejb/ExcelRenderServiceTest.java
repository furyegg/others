package com.lombardrisk.xbrl.render.ejb;

import com.google.common.collect.Lists;
import com.lombardrisk.xbrl.model.XbrlDataPointLight;
import com.lombardrisk.xbrl.model.XbrlTableLight;
import com.lombardrisk.xbrl.render.model.XbrlRenderRequest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExcelRenderServiceTest {

    private Collection<XbrlTableLight> tables = Lists.newArrayList();
    private List<XbrlDataPointLight> dataPoints = Lists.newArrayList();

    @Test
    public void testGetExcelRenderedXbrl() throws Exception {
        ExcelRenderService excelRenderService = new ExcelRenderService();
        excelRenderService.init();
        Workbook wb = excelRenderService.getExcelRenderedXbrl(createXbrlRenderRequest());
//        OutputStream out = new FileOutputStream(new File("test.xlsx"));
//        wb.write(out);
        checkValue(wb.getSheet("C 07.00.a(001)"), "E10", 123);
        checkValue(wb.getSheet("C 07.00.a(002)"), "E10", 456);

        checkValue(wb.getSheet("C 09.01.a(UK)"), "E8", 100);
        checkValue(wb.getSheet("C 09.01.a(US)"), "E8", 200);

        checkValue(wb.getSheet("C 30.00"), "E8", 1);
        checkValue(wb.getSheet("C 30.00"), "F8", 1000);
        checkValue(wb.getSheet("C 30.00"), "H8", 2000);

        checkValue(wb.getSheet("C 30.00"), "E9", 2);
        checkValue(wb.getSheet("C 30.00"), "F9", 3000);
        checkValue(wb.getSheet("C 30.00"), "H9", 4000);

        checkValue(wb.getSheet("C 29.00"), "E10", 1);
        checkValue(wb.getSheet("C 29.00"), "F10", "A");
        checkValue(wb.getSheet("C 29.00"), "I10", "555");
        checkValue(wb.getSheet("C 29.00"), "J10", "666");

        checkValue(wb.getSheet("C 29.00"), "E11", 1);
        checkValue(wb.getSheet("C 29.00"), "F11", "B");
        checkValue(wb.getSheet("C 29.00"), "I11", "777");
        checkValue(wb.getSheet("C 29.00"), "J11", "888");
    }

    private XbrlRenderRequest createXbrlRenderRequest() {
        XbrlTableLight openZ = mockXbrlTable("C 09.01.a", true, false);
        XbrlTableLight closedZ = mockXbrlTable("C 07.00.a", false, false);
        XbrlTableLight openY = mockXbrlTable("C 30.00", false, true);
        XbrlTableLight openY2 = mockXbrlTable("C 29.00", false, true);

        //open z
        mockXbrlDataPoint("88489", "100", "UK", Lists.newArrayList(openZ));
        mockXbrlDataPoint("88489", "200", "US", Lists.newArrayList(openZ));

        //closed z
        mockXbrlDataPoint("76788", "123", null, Lists.newArrayList(closedZ));//001
        mockXbrlDataPoint("88533", "456", null, Lists.newArrayList(closedZ));//002

        //open y row 1
        mockXbrlDataPoint("84950", "1000", "1(1)", Lists.newArrayList(openY));
        mockXbrlDataPoint("84952", "2000", "1(1)", Lists.newArrayList(openY));

        //open y row 2
        mockXbrlDataPoint("84950", "3000", "2(1)", Lists.newArrayList(openY));
        mockXbrlDataPoint("84952", "4000", "2(1)", Lists.newArrayList(openY));

        //open y2 row 1
        mockXbrlDataPoint("85015", "555", "1(1)$|$A(2)", Lists.newArrayList(openY2));
        mockXbrlDataPoint("85014", "666", "1(1)$|$A(2)", Lists.newArrayList(openY2));

        //open y2 row 2
        mockXbrlDataPoint("85015", "777", "1(1)$|$B(2)", Lists.newArrayList(openY2));
        mockXbrlDataPoint("85014", "888", "1(1)$|$B(2)", Lists.newArrayList(openY2));

        XbrlRenderRequest request = new XbrlRenderRequest(tables, dataPoints, "");
        return request;
    }

    private XbrlTableLight mockXbrlTable(String tableCode, boolean openZ, boolean openY) {
        XbrlTableLight table = new XbrlTableLight(tableCode, openY, openZ);
        tables.add(table);
        return table;
    }

    private XbrlDataPointLight mockXbrlDataPoint(String id, String value, String openCode, List<XbrlTableLight> tables) {
        XbrlDataPointLight dp = new XbrlDataPointLight(id, value, openCode, tables);
        dataPoints.add(dp);
        return dp;
    }

    private static void checkValue(Sheet sheet, String cellRef, Object expectedValue) {
        CellReference ref = new CellReference(cellRef);
        Row r = sheet.getRow(ref.getRow());
        if (r != null) {
            Cell c = r.getCell(ref.getCol());
            if(c.getCellType() == Cell.CELL_TYPE_NUMERIC){
                assertEquals((Double) expectedValue, c.getNumericCellValue(), 0.001);
            }else{
                assertEquals(String.valueOf(expectedValue), c.getRichStringCellValue().getString());
            }
        } else {
            throw new AssertionError("Could not find cell at " + cellRef);
        }
    }
}