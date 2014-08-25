package com.lombardrisk.xbrl.render.ejb;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.model.XbrlDataPointLight;
import com.lombardrisk.xbrl.model.XbrlModelUtils;
import com.lombardrisk.xbrl.model.XbrlTableLight;
import com.lombardrisk.xbrl.render.model.XbrlRenderRequest;
import com.lombardrisk.xbrl.render.model.exception.XbrlRenderException;
import com.lombardrisk.xbrl.render.util.AlphanumComparator;
import com.lombardrisk.xbrl.render.util.ExcelUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cesar on 09/06/2014.
 */
@Stateless
public class ExcelRenderService {
    private static final Logger log = LoggerFactory.getLogger(ExcelRenderService.class);
    public static final String OPEN_SHEET_NAME_PATTERN = "{0}({1})";
    public static final Pattern OPEN_COPE_PATTERN = Pattern.compile("^[\\w \\.]*\\(([\\w]*)\\)$");
    public static final String ROW_START_DELIMITER = "999";
    private static final Splitter OPEN_CODE_SPLITTER = Splitter.on(XbrlModelUtils.XBRL_OPEN_DELIMETER);
    private static final String OPEN_CODE_OFFSET_PATTERN_STRING = "^([\\w]*)\\" + XbrlModelUtils.XBRL_OPEN_DELIMITER_OPEN_BOUNDARY + "([\\d]*)\\" + XbrlModelUtils.XBRL_OPEN_DELIMITER_CLOSE_BOUNDARY;
    private static final Pattern OPEN_CODE_OFFSET_PATTERN = Pattern.compile(OPEN_CODE_OFFSET_PATTERN_STRING);
    private byte[] cachedExcelTemplate = null;

    @PostConstruct
    public void init() {
        String templatePath = Config.INSTANCE.getString("eba.templates.path");
        final String excelTemplatesFileName = "ebaTemplates.xlsx";
        if (!templatePath.endsWith(File.separator)) {
            templatePath += File.separator;
        }
        final File excelFile = new File(templatePath + excelTemplatesFileName);
        try {
            if (!excelFile.exists()) {
                throw new IOException(MessageFormat.format("Not fond {0}", excelFile.getAbsolutePath()));
            }
            cachedExcelTemplate = FileUtils.readFileToByteArray(excelFile);
        } catch (IOException e) {
            log.error("Could not read excel templates files", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Workbook getExcelRenderedXbrl(XbrlRenderRequest request) throws IOException, XbrlRenderException {
        if (cachedExcelTemplate == null) {
            throw new IOException("Excel file not initialised");
        }
        final Workbook wb = getExcelTemplate();

        //1) we remove the unused sheet and only keep those that are referenced in the filling indicator
        removeUnwantedSheets(request, wb);

        if (wb.getNumberOfSheets() == 0) {
            throw new XbrlRenderException("No sheet matched the filling indicator(s)");
        }

        //2) for the openZ return, we gather the list of instances
        prepareOpenSheets(request, wb);

        //3) for the openY return, we gather the list of instances so that they will be processed separately
        //SheetName -> openCode -> List<XbrlDataPointLight
        Map<String, SortedSetMultimap<String, XbrlDataPointLight>> extendedGridMap = prepareExtendedGrids(request);

        //4) process openZ and closedZ returns. OpenY returns are ignored
        processWorkbook(request, wb, extendedGridMap);

        //5) process openY returns;
        processExtendedGridSheets(wb, extendedGridMap, request);
        return wb;
    }

    private void processExtendedGridSheets(Workbook wb, Map<String, SortedSetMultimap<String, XbrlDataPointLight>> extendedGridMap, XbrlRenderRequest request) throws XbrlRenderException {
        final Map<Integer, Long> dpmIdToColumnNumber = new HashMap<>();
        for (int sheetNumber = 0; sheetNumber < wb.getNumberOfSheets(); sheetNumber++) {
            final Sheet sheet = wb.getSheetAt(sheetNumber);
            if (extendedGridMap.containsKey(sheet.getSheetName())) {
                processExtendedGridSheet(extendedGridMap, dpmIdToColumnNumber, sheet, request);
            }
        }
    }

    private void processExtendedGridSheet(Map<String, SortedSetMultimap<String, XbrlDataPointLight>> extendedGridMap, Map<Integer, Long> dpmIdToColumnNumber, Sheet sheet, XbrlRenderRequest request) throws XbrlRenderException {
        int headerRow = -1;
        int maxColumnIdx = -1;
        int minColumnIdx = -1;
        int columnStart = -1;
        //first we need to locate the header row containing the dpm
        log.debug("Doing extended grid " + sheet.getSheetName());
        for (Row row : sheet) {
            for (Cell cell : row) {
                final CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    if (headerRow == -1) {
                        headerRow = row.getRowNum();
                        minColumnIdx = cell.getColumnIndex();
                        log.debug(MessageFormat.format("Found first numeric cell at {0} for sheet {1}", cellRef.formatAsString(), sheet.getSheetName()));
                    }
                    if (headerRow != -1) {
                        //once the header row is found, we store the mapping dpmId -> columnIndex
                        final double dpmId = cell.getNumericCellValue();
                        if (dpmId != 0) {
                            dpmIdToColumnNumber.put(cell.getColumnIndex(), (long) dpmId);
                        }
                        maxColumnIdx = Math.max(maxColumnIdx, cell.getColumnIndex());
                    }
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    if (ROW_START_DELIMITER.equals(cell.getRichStringCellValue().getString())) {
                        columnStart = cell.getColumnIndex();
                    }
                }
            }
            if (headerRow != -1) {
                break;
            }
        }
        final Ordering<XbrlDataPointLight> naturalOrdering = XbrlDataPointLight.getNaturalOrdering();
        //now, starting from the header row, we populate the data for each openCode
        final SortedSetMultimap<String, XbrlDataPointLight> dpmMaps = extendedGridMap.get(sheet.getSheetName());
        for (String openCode : dpmMaps.keySet()) {
            log.debug("doing open code: " + openCode);
            //we put the DataPoints in a list so we can use Binary Search (the SortedSetMultimap ensure that the set is sorted)
            final List<XbrlDataPointLight> dataPointLists = new ArrayList<>(dpmMaps.get(openCode));
            Row row = sheet.getRow(headerRow);
            if (row == null) {
                row = sheet.createRow(headerRow);

            }
            populateGridKeyCells(columnStart, openCode, row);
            for (int cellId = minColumnIdx; cellId <= maxColumnIdx; cellId++) {
                final Cell cell = ExcelUtils.getOrCreateCell(cellId, row);
                final CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
                final Long dpm = dpmIdToColumnNumber.get(cell.getColumnIndex());
                if (dpm != null) {
                    final int idx = Collections.binarySearch(dataPointLists, new XbrlDataPointLight(dpm, openCode), naturalOrdering);
                    if (idx >= 0) {
                        final XbrlDataPointLight dp = dataPointLists.get(idx);
                        cell.setCellValue(dp.getValue());
                        log.debug(MessageFormat.format("Setting cell {0} to ''{1}''", cellRef.formatAsString(), dp.getValue()));
                    } else {
                        setDefaultValue(cell, cellRef, dpm, request);
                    }
                }
            }
            headerRow++;
        }
    }

    private void setDefaultValue(Cell cell, CellReference cellRef, long dpmId, XbrlRenderRequest request) {
        if (BooleanUtils.isTrue(request.getDataPointNumericTypeMap().get(String.valueOf(dpmId)))) {
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(0);
            log.trace(MessageFormat.format("No datapoint found for cell {0}, setting it to 0", cellRef.formatAsString()));
        } else {
            final String defaultValue = Config.INSTANCE.getString("xbrl.non.numeric.default.value");
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(defaultValue);
            log.trace(MessageFormat.format("No datapoint found for cell {0}, setting it to ''{1}''", cellRef.formatAsString(), defaultValue));
        }

    }

    private void populateGridKeyCells(int columnStart, String openCode, Row row) throws XbrlRenderException {
        if (columnStart == -1) {
            throw new XbrlRenderException("Could not find start of row " + ROW_START_DELIMITER);
        }
        final Iterable<String> openCodes = OPEN_CODE_SPLITTER.split(openCode);
        for (String code : openCodes) {
            final Matcher m = OPEN_CODE_OFFSET_PATTERN.matcher(code);
            if (m.matches()) {
                try {
                    final String codeValue = m.group(1);
                    final int offset = Integer.parseInt(m.group(2));
                    final Cell openCodeCell = ExcelUtils.getOrCreateCell(columnStart + offset, row);
                    openCodeCell.setCellValue(codeValue);
                } catch (NumberFormatException e) {
                    throw new XbrlRenderException("Malformed open code " + code, e);
                }
            } else {
                throw new XbrlRenderException("Malformed open code " + code);
            }
        }
    }

    private Map<String, SortedSetMultimap<String, XbrlDataPointLight>> prepareExtendedGrids(XbrlRenderRequest request) {
        //SheetName -> openCode -> List<XbrlDataPointLight
        final Map<String, SortedSetMultimap<String, XbrlDataPointLight>> extendedGridMap = new HashMap<>();
        for (XbrlDataPointLight dp : request.getXbrlDataPointLights()) {
            for (XbrlTableLight table : dp.getTableCodes()) {
                if (table.isOpenY()) {
                    SortedSetMultimap<String, XbrlDataPointLight> dataPointMap = extendedGridMap.get(table.getTableCode());
                    if (dataPointMap == null) {
                        dataPointMap = TreeMultimap.create(new AlphanumComparator(), XbrlDataPointLight.getNaturalOrdering());
                        extendedGridMap.put(table.getTableCode(), dataPointMap);
                    }
                    dataPointMap.put(dp.getOpenCode(), dp);
                }
            }
        }
        return extendedGridMap;
    }

    private void prepareOpenSheets(XbrlRenderRequest request, Workbook wb) throws XbrlRenderException {
        final SetMultimap<String, String> openSheetMap = HashMultimap.create();
        String sheetName;
        for (XbrlDataPointLight dp : request.getXbrlDataPointLights()) {
            for (XbrlTableLight xbrlTableLight : dp.getTableCodes()) {
                if (xbrlTableLight.isOpenZ()) {
                    sheetName = getOpenSheetName(dp, xbrlTableLight);
                    openSheetMap.put(xbrlTableLight.getTableCode(), sheetName);
                }
            }
        }
        //this will delete the original sheet
        ExcelUtils.cloneSheets(wb, openSheetMap, true);
    }

    private void processWorkbook(XbrlRenderRequest request, Workbook wb, Map<String, ?> extendedGridMap) throws XbrlRenderException {
        for (int sheetNumber = 0; sheetNumber < wb.getNumberOfSheets(); sheetNumber++) {
            final Sheet sheet = wb.getSheetAt(sheetNumber);
            if (extendedGridMap.containsKey(sheet.getSheetName())) {
                continue;
            }
            XbrlTableLight matchingTable = null;
            for (XbrlTableLight tableLight : request.getXbrlTableLights()) {
                if (sheet.getSheetName().contains(tableLight.getTableCode())) {
                    matchingTable = tableLight;
                    break;
                }
            }
            if (matchingTable != null) {
                log.debug("Doing sheet (closed Y) " + sheet.getSheetName());
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        processCell(request, sheet, row, cell, matchingTable);
                    }
                }
            } else {
                throw new XbrlRenderException("Could not find a matching table code for sheet " + sheet.getSheetName());
            }
        }
    }

    private void processCell(XbrlRenderRequest request, Sheet sheet, Row row, Cell cell, XbrlTableLight table) throws XbrlRenderException {
        final CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
        //this method assumes that all the datapoint cell in the excel spreadsheet are of numeric type.
        final Double numericValue = ExcelUtils.getNumericValue(cell);
        if (numericValue != null) {
            final long dpmId = numericValue.longValue();
            //We use binary search to efficiently find the datapoint (because this list can be large (~1000 entries)
            XbrlDataPointLight searchKey;
            if (table.isOpenZ()) {
                final String openCode = extractOpenCode(sheet.getSheetName());
                searchKey = new XbrlDataPointLight(dpmId, openCode);
            } else {
                searchKey = new XbrlDataPointLight(dpmId, null);
            }
            final int idx = Collections.binarySearch(request.getXbrlDataPointLights(), searchKey, XbrlDataPointLight.getNaturalOrdering());
            if (idx >= 0) {
                final XbrlDataPointLight dp = request.getXbrlDataPointLights().get(idx);
                final String openSheetName = getOpenSheetName(dp, table);
                final boolean openZ = table.isOpenZ() && sheet.getSheetName().equals(openSheetName);
                final boolean closedZ = openZ || (!table.isOpenZ() && sheet.getSheetName().contains(table.getTableCode()));
                if (openZ || closedZ) {
//                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                    cell.setCellValue(dp.getValue());

                    log.debug(MessageFormat.format("Setting cell {0} to ''{1}''", cellRef.formatAsString(), dp.getValue()));
                } else {
                    final String msg = MessageFormat.format("Datapoint {0} was referenced in sheet {1} but doesn't " +
                            "match any of its table codes", dp.getId(), sheet.getSheetName());
                    throw new XbrlRenderException(msg);
                }
            } else {
                setDefaultValue(cell, cellRef, dpmId, request);
            }
        }
    }

    private void removeUnwantedSheets(XbrlRenderRequest request, Workbook wb) {
        Collection<Predicate<Sheet>> predicates = new ArrayList<>();
        for (final XbrlTableLight xbrlTable : request.getXbrlTableLights()) {
            predicates.add(new Predicate<Sheet>() {
                @Override
                public boolean apply(Sheet sheet) {
                    return sheet.getSheetName().contains(xbrlTable.getTableCode());
                }
            });
        }
        ExcelUtils.keepSheets(wb, predicates);
    }

    public Workbook getExcelTemplate() throws IOException {
        try {
            return WorkbookFactory.create(new ByteArrayInputStream(cachedExcelTemplate));
        } catch (InvalidFormatException e) {
            throw new IOException(e);
        }
    }

    private static String getOpenSheetName(XbrlDataPointLight dp, XbrlTableLight tableLight) throws XbrlRenderException {
        if(StringUtils.isEmpty(dp.getOpenCode())) {
            throw new XbrlRenderException(MessageFormat.format("Couldn't find open code for data point {0}", dp.getId()));
        }
        return MessageFormat.format(OPEN_SHEET_NAME_PATTERN, tableLight.getTableCode(), dp.getOpenCode());
    }

    private static String extractOpenCode(String sheetName) {
        Matcher m = OPEN_COPE_PATTERN.matcher(sheetName);
        if (m.matches()) {
            return m.group(1);
        } else {
            return null;
        }
    }

}
