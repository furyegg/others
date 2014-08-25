package com.lombardrisk.xbrl.render.util;

import com.google.common.base.Predicate;
import com.google.common.collect.SetMultimap;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Cesar on 09/06/2014.
 */
public final class ExcelUtils {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    private ExcelUtils() {
    }


    /**
     * Goes through the given Workbook and keep the sheet that match at least one of the given predicates. The other
     * sheets are removed from the
     *
     * @param workbook   workbook
     * @param predicates if at least one predicate applies, the sheet is kept
     */
    public static void keepSheets(Workbook workbook, Iterable<? extends Predicate<Sheet>> predicates) {
        for (int sheetNumber = 0; sheetNumber < workbook.getNumberOfSheets(); sheetNumber++) {
            final Sheet sheet = workbook.getSheetAt(sheetNumber);
            boolean keepSheet = false;
            for (Predicate<Sheet> p : predicates) {
                if (p.apply(sheet)) {
                    keepSheet = true;
                    log.debug("Keeping sheet {} ", sheet.getSheetName());
                    break;
                }
            }
            if (!keepSheet) {
                workbook.removeSheetAt(sheetNumber);
                sheetNumber--;
            }
        }
    }

    public static Double getNumericValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return null;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return null;
                } else {
                    return cell.getNumericCellValue();
                }
            case Cell.CELL_TYPE_BOOLEAN:
                return null;
            case Cell.CELL_TYPE_FORMULA:
                return null;
            default:
                return null;
        }
    }

    public static void cloneSheet(Workbook wb, int sheetAt, Collection<String> cloneNames, boolean deleteOriginal) {
        final Sheet original = wb.getSheetAt(sheetAt);
        for (String cloneName : cloneNames) {
            final Sheet newSheet = wb.cloneSheet(sheetAt);
            final int index = wb.getSheetIndex(newSheet);
            wb.setSheetName(index, cloneName);
        }

        if (deleteOriginal) {
            wb.removeSheetAt(sheetAt);
        }

    }

    public static void cloneSheets(Workbook wb, SetMultimap<String, String> cloneNames, boolean deleteOriginal) {
        for (int sheetNumber = 0; sheetNumber < wb.getNumberOfSheets(); sheetNumber++) {
            final String sheetName = wb.getSheetName(sheetNumber);
            final Set<String> names = cloneNames.get(sheetName);
            if (!names.isEmpty()) {
                cloneSheet(wb, sheetNumber, names, deleteOriginal);
                if (deleteOriginal) {
                    sheetNumber--;
                }
            }
        }
    }

    public static Cell getOrCreateCell(int columnId, Row row) {
        Cell c = row.getCell(columnId);
        if(c == null) {
            c = row.createCell(columnId);
        }
        return c;
    }
}
