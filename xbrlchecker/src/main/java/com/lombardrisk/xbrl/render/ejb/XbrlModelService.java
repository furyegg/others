package com.lombardrisk.xbrl.render.ejb;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.util.EntryIterableBuilder;
import com.healthmarketscience.jackcess.util.Joiner;
import com.lombardrisk.xbrl.checker.EbaConstants;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.csv.CsvUtil;
import com.lombardrisk.xbrl.model.*;
import com.lombardrisk.xbrl.render.csv.DataPointCsvEntry;
import com.lombardrisk.xbrl.render.csv.DataPointCsvEntryDescriptor;
import com.lombardrisk.xbrl.render.csv.OpenCodeEntry;
import com.lombardrisk.xbrl.render.util.AxisOrdinateComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by Bartosz Jedrzejewski on 06/06/14.
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class XbrlModelService implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(XbrlModelService.class);

    private Database dpmDB;
    private final Map<String, List<String>> dataPointsMap = new HashMap<>();
    private final Multimap<String, OpenCodeEntry> openCodeMultiMap = HashMultimap.create();
    private final Map<String, Boolean> isMetricNumericMap = new HashMap<>();
    private final Map<String, Boolean> isDatapointNumericMap = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {
        final String dpmPath = Config.INSTANCE.getString("dpm.access.location");
        final File dpmFile = new File(dpmPath);
        if (dpmFile == null) {
            throw new IOException("Could not load DPM database at location: " + dpmFile.getAbsolutePath());
        }
        dpmDB = DatabaseBuilder.open(dpmFile);

        loadDpmMappings();
        loadOpenCodes();
        loadIsMetricNumericMap();
        loadIsDatapointNumericMap();
    }

    private void loadIsDatapointNumericMap() throws IOException{
        Table dataPointVersionTable = dpmDB.getTable(EbaConstants.DATAPOINT_VERSION_TABLE);
        Table metricTable = dpmDB.getTable(EbaConstants.METRIC_TABLE);
        for(Row dataPointRow : dataPointVersionTable) {
            String dataPointId = String.valueOf(dataPointRow.get("DataPointVID"));
            Row metricRow = CursorBuilder.findRow(metricTable,
                    Collections.singletonMap("MetricID", dataPointRow.get("MetricID")));
            Boolean isNumeric = false;
            String dataTypeNumber = String.valueOf(metricRow.get("DataTypeID"));
            if(dataTypeNumber.equals("3") || dataTypeNumber.equals("4") || dataTypeNumber.equals("5")){
                isNumeric = true;
            }
            isDatapointNumericMap.put(dataPointId, isNumeric);
        }
    }

    private void loadIsMetricNumericMap() throws IOException {
        Table metricTable = dpmDB.getTable(EbaConstants.METRIC_TABLE);
        Table memberTable = dpmDB.getTable(EbaConstants.MEMBER_TABLE);
        for(Row metricRow : metricTable){
            Boolean isNumeric = false;
            String dataTypeNumber = String.valueOf(metricRow.get("DataTypeID"));
            if(dataTypeNumber.equals("3") || dataTypeNumber.equals("4") || dataTypeNumber.equals("5")){
                isNumeric = true;
            }
            Row memberRow = CursorBuilder.findRow(memberTable,
                    Collections.singletonMap("MemberID", metricRow.get("MetricID")));
            String metricCode = String.valueOf(memberRow.get("MemberCode"));
            isMetricNumericMap.put(metricCode, isNumeric);
        }
    }

    private void loadOpenCodes() throws IOException {
        Multimap<String, OpenCodeEntry> openCodes =  buildOpenCodeEntries();
        openCodeMultiMap.putAll(openCodes);
        log.info(MessageFormat.format("Loaded {0} open code entries", openCodeMultiMap.size()));
    }

    private void loadDpmMappings() throws IOException {
        final File dpmMappingsFile = new File(Config.INSTANCE.getString("dpm.mappings.csv.location"));
        if (!dpmMappingsFile.exists()) {
            throw new IOException("Could not load DPM mappings file at " + dpmMappingsFile.getAbsolutePath());
        }
        final Reader reader = new FileReader(dpmMappingsFile);
        final List<DataPointCsvEntry> dataPointCsvEntries = CsvUtil.parse(reader, new DataPointCsvEntryDescriptor(), true);
        for (DataPointCsvEntry dp : dataPointCsvEntries) {
            dataPointsMap.put(dp.getKey(), dp.getElements());
        }
        log.info(MessageFormat.format("Loaded {0} entries from {1}", dataPointsMap.size(), dpmMappingsFile.getAbsolutePath()));
    }

    private Multimap<String, OpenCodeEntry> buildOpenCodeEntries() throws IOException {
        Multimap<String, OpenCodeEntry> openCodeEntries = HashMultimap.create();
        final Table tableVersionTable = dpmDB.getTable(EbaConstants.TABLE_VERSION_TABLE);
        for (Row tableRow : tableVersionTable) {
            String tableVID = String.valueOf(tableRow.get("TableVID"));
            Table axisTable = dpmDB.getTable(EbaConstants.AXIS_TABLE);
            final Joiner axisJoiner = Joiner.create(tableVersionTable.getForeignKeyIndex(axisTable));
            final EntryIterableBuilder axisRows = axisJoiner.findRows(tableRow);
            Row xAxisRow = null;
            boolean isOpenGrid = false;
            for(Row axisRow : axisRows){
                String axisOrientation = String.valueOf(axisRow.get("AxisOrientation"));
                if(axisOrientation.equals("X")){
                    xAxisRow = axisRow;
                } else if(axisOrientation.equals("Y")){
                    if(String.valueOf(axisRow.get("IsOpenAxis")).equals("true")){
                        isOpenGrid = true;
                    }
                }
            }
            if(isOpenGrid){
                Table axisOrdinateTable = dpmDB.getTable(EbaConstants.AXIS_ORDINATE_TABLE);
                final Joiner ordinateJoiner = Joiner.create(axisTable.getForeignKeyIndex(axisOrdinateTable));
                final EntryIterableBuilder ordinateRows = ordinateJoiner.findRows(xAxisRow);
                List<Row> ordinateList = new ArrayList<>();
                for(Row ordinateRow : ordinateRows){
                    if(!String.valueOf(ordinateRow.get("IsAbstractHeader")).equals("true")){
                        ordinateList.add(ordinateRow);
                    }
                }
                Collections.sort(ordinateList, new AxisOrdinateComparator());
                for(int i = 0; i < ordinateList.size(); i++){
                    Row currentOrdinate = ordinateList.get(i);
                    if(String.valueOf(currentOrdinate.get("IsRowKey")).equals("true")){
                        OpenCodeEntry openCodeEntry = new OpenCodeEntry();
                        //How many places from the label - 1 = next to the label
                        openCodeEntry.setTableOffset(i+1);
                        openCodeEntry.setDimensionCode(String.valueOf(currentOrdinate.get("CategorisationKey")).split("999")[0]);
                        openCodeEntries.put(String.valueOf(tableRow.get("XbrlTableCode")),openCodeEntry);
                    }
                }
            }
        }
        return openCodeEntries;
    }

    public List<String> findDataPointInfo(String dataPointKey) {
        return dataPointsMap.get(dataPointKey);
    }

    public Map<String, List<String>> getDataPointsMap() {
        return dataPointsMap;
    }

    public Collection<XbrlTable> buildXbrlTableFromFillingIndicatorCode(String fillingIndicatorCode) throws IOException {
        Collection<XbrlTable> xbrlTables = new ArrayList<>();
        final Table tableVersionTable = dpmDB.getTable(EbaConstants.TABLE_VERSION_TABLE);
        for (Row row : tableVersionTable) {
            if (String.valueOf(row.get("XbrlFilingIndicatorCode")).equals(fillingIndicatorCode)) {
                xbrlTables.add(buildXbrlTable(String.valueOf(row.get("XbrlTableCode"))));
            }
        }
        return xbrlTables;
    }

    /**
     * Creates a map, where the first string is datapoint key:
     * <p/>
     * eba_dim:APL:x20-eba_dim:BAS:x6-eba_dim:MCY:x469-eba_met:mi53
     * eba_dim:BAS:x7-eba_dim:MCY:x285-eba_met:mi53
     * eba_dim:BAS:x1-eba_dim:MCY:x498-eba_met:md103
     * <p/>
     * The array of strings is: first, the dataPointID and then the tables that it appears in
     *
     * @return
     */
    public Map<String, List<String>> createDataPointMap() throws IOException {
        Collection<XbrlTable> xbrlTables = new ArrayList<>();
        Map<String, List<String>> dataPointsMap = new HashMap<>();
        final Table tableVersionTable = dpmDB.getTable(EbaConstants.TABLE_VERSION_TABLE);
        int count = 0;
        for (Row row : tableVersionTable) {
            xbrlTables.add(buildXbrlTable(String.valueOf(row.get("XbrlTableCode"))));
            count++;
            if (count == 999) {
                break;
            }
        }
        for (XbrlTable xbrlTable : xbrlTables) {
            for (XbrlDataPoint xbrlDataPoint : xbrlTable.getDataPoints()) {
                String key = createXbrlDataPointKey(xbrlDataPoint);
                if (dataPointsMap.containsKey(key)) {
                    dataPointsMap.get(key).add(xbrlTable.getTableCode());
                } else {
                    List<String> dpList = new ArrayList<>();
                    dpList.add(xbrlDataPoint.getDataPointID());
                    dpList.add(xbrlTable.getTableCode());
                    dataPointsMap.put(key, dpList);
                }
            }
        }
        return dataPointsMap;
    }

    public List<XbrlTableLight> creatXbrlTableLights(String fillingIndicator) throws IOException {
        List<XbrlTableLight> tableLights = new ArrayList<>();
        final Table tableVersionTable = dpmDB.getTable(EbaConstants.TABLE_VERSION_TABLE);
        for (Row row : tableVersionTable) {
             if (String.valueOf(row.get("XbrlFilingIndicatorCode")).equals(fillingIndicator)) {
                String xbrlTableCode = String.valueOf(row.get("XbrlTableCode"));
                String xbrlTableVid = String.valueOf(row.get("TableVID"));
                boolean isOpenYAxis = checkIfOpenYAxis(xbrlTableVid);
                boolean isOpenZAxis = checkIfOpenZAxis(xbrlTableVid);
                tableLights.add(new XbrlTableLight(xbrlTableCode, isOpenYAxis, isOpenZAxis));
            }
        }
        return tableLights;
    }


    private String createXbrlDataPointKey(XbrlDataPoint xbrlDataPoint) {
        return XbrlModelUtils.createDatapointKey(xbrlDataPoint.getDataPointContext().getXbrlDimensionMemberSet(), xbrlDataPoint.getDataPointMetric());
    }

    public XbrlTable buildXbrlTable(String xbrlTableCode) throws IOException {
        final Table tableVersionTable = dpmDB.getTable(EbaConstants.TABLE_VERSION_TABLE);
        Row tableRow = CursorBuilder.findRow(tableVersionTable, Collections.singletonMap("XbrlTableCode", xbrlTableCode));
        if (tableRow == null) {
            throw new IOException("Couldn't find table with code " + xbrlTableCode);
        }
        /*String tableVID = String.valueOf(tableRow.get("TableVID"));*/
        final Table tableCellTable = dpmDB.getTable(EbaConstants.TABLE_CELL_TABLE);
        //Join on the tableCellTable
        final Joiner cellsJoiner = Joiner.create(tableVersionTable.getForeignKeyIndex(tableCellTable));
        //we get the rows in tableCellTable for the tableRow
        final EntryIterableBuilder cellRows = cellsJoiner.findRows(tableRow);
        final Set<XbrlDataPoint> xbrlDataPoints = new HashSet<>();
        for (Row cellRow : cellRows) {
            if (cellRow.get("DataPointID") != null) {
                xbrlDataPoints.add(buildXbrlDataPoint(String.valueOf(cellRow.get("DataPointID"))));
            }
        }
        XbrlDataPoint[] xbrlDataPointsArray = new XbrlDataPoint[(xbrlDataPoints.size())];
        xbrlDataPointsArray = xbrlDataPoints.toArray(xbrlDataPointsArray);
        XbrlDimension zAxisCriteria = getOpenAxisCriteria(xbrlDataPointsArray[0]);
        boolean isOpenYAxis = checkIfOpenYAxis(String.valueOf(tableRow.get("TableVID")));
        XbrlTable xbrlTable = new XbrlTable(xbrlTableCode, xbrlDataPoints, zAxisCriteria, isOpenYAxis);
        return xbrlTable;
    }

    /*
    All dataPoints will have to have this 'wildcard' value if
    there are page instances, so only one dataPoint is needed to check.
     */
    public XbrlDimension getOpenAxisCriteria(XbrlDataPoint dataPoint) {
        XbrlDimension dimension = null;
        for (XbrlDimensionMember dimensionMember : dataPoint.getDataPointContext().getXbrlDimensionMemberSet()) {
            if (dimensionMember.getValue().equals("x999")) {
                dimension = dimensionMember.getDimension();
                break;
            }
        }
        return dimension;
    }

    //TODO - use some enum, make it clever
    public boolean checkIfOpenYAxis(String tableVID) throws IOException {
        final Table axisTable = dpmDB.getTable(EbaConstants.AXIS_TABLE);
        final Map<String, Object> rowCriteria = new HashMap<>();
        rowCriteria.put("TableVID", Integer.parseInt(tableVID));
        rowCriteria.put("AxisOrientation", "Y");
        Row axisRow = CursorBuilder.findRow(axisTable, rowCriteria);
        if (String.valueOf(axisRow.get("IsOpenAxis")).equals("true")) {
            return true;
        }
        return false;
    }

    //TODO - use some enum, make it clever
    public boolean checkIfOpenZAxis(String tableVID) throws IOException {
        final Table axisTable = dpmDB.getTable(EbaConstants.AXIS_TABLE);
        final Map<String, Object> rowCriteria = new HashMap<>();
        rowCriteria.put("TableVID", Integer.parseInt(tableVID));
        rowCriteria.put("AxisOrientation", "Z");
        Row axisRow = CursorBuilder.findRow(axisTable, rowCriteria);
        if (axisRow == null) {
            return false;
        }
        if (String.valueOf(axisRow.get("IsOpenAxis")).equals("true")) {
            return true;
        }
        return false;
    }


    public XbrlDataPoint buildXbrlDataPoint(String dataPointVID) throws IOException {
        final Table dataPointVersionTable = dpmDB.getTable(EbaConstants.DATAPOINT_VERSION_TABLE);
        Row dataPointRow = CursorBuilder.findRow(dataPointVersionTable,
                Collections.singletonMap("DataPointID", Integer.parseInt(dataPointVID)));
        String metricID = String.valueOf(dataPointRow.get("MetricID"));
        XbrlMetric dpMetric = buildXbrlMetric(metricID);
        String contextID = String.valueOf(dataPointRow.get("ContextID"));
        XbrlContext dpContext = buildXbrlContext(contextID);
        XbrlDataPoint dataPoint = new XbrlDataPoint(dataPointVID, dpContext, dpMetric);
        return dataPoint;
    }

    public XbrlMetric buildXbrlMetric(String metricId) throws IOException {
        final Table memberTable = dpmDB.getTable(EbaConstants.MEMBER_TABLE);
        Row memberRow =
                CursorBuilder.findRow(memberTable, Collections.singletonMap("MemberID", Integer.parseInt(metricId)));
        if (memberRow == null) {
            throw new IOException("Couldn't find metric/member with id " + metricId);
        }
        XbrlMetric metric = new XbrlMetric(String.valueOf(memberRow.get("MemberCode")));
        return metric;
    }

    public XbrlContext buildXbrlContext(String contextId) throws IOException {
        final Table contextOfDataPoints = dpmDB.getTable(EbaConstants.CONTEXT_OF_DATAPOINTS_TABLE);
        Row contextOfDataPointsRow =
                CursorBuilder.findRow(contextOfDataPoints, Collections.singletonMap("ContextID", Integer.parseInt(contextId)));
        final Table contextDefinitionTable = dpmDB.getTable(EbaConstants.CONTEXT_DEFINITION_TABLE);
        final Joiner contextJoiner = Joiner.create(contextOfDataPoints.getForeignKeyIndex(contextDefinitionTable));
        final EntryIterableBuilder contextRows = contextJoiner.findRows(contextOfDataPointsRow);
        Set<XbrlDimensionMember> xbrlDimensionMemberSet = new HashSet<>();
        for (Row contextRow : contextRows) {
            xbrlDimensionMemberSet
                    .add(buildXbrlDimensionMember(String.valueOf(contextRow.get("DimensionID"))
                            , String.valueOf(contextRow.get("MemberID"))));
        }
        XbrlContext context = new XbrlContext(xbrlDimensionMemberSet);
        return context;
    }

    public XbrlDimensionMember buildXbrlDimensionMember(String dimensionID, String memberID) throws IOException {
        final Table memberTable = dpmDB.getTable(EbaConstants.MEMBER_TABLE);
        Row memberRow =
                CursorBuilder.findRow(memberTable, Collections.singletonMap("MemberID", Integer.parseInt(memberID)));
        XbrlDimensionMember dimensionMember = new XbrlDimensionMember(buildXbrlDimension(dimensionID)
                , String.valueOf(memberRow.get("MemberCode")));
        return dimensionMember;
    }

    public XbrlDimension buildXbrlDimension(String dimensionID) throws IOException {
        final Table dimensionTable = dpmDB.getTable(EbaConstants.DIMENSION_TABLE);
        Row dimensionRow =
                CursorBuilder.findRow(dimensionTable, Collections.singletonMap("DimensionID", Integer.parseInt(dimensionID)));
        XbrlDimension dimension = new XbrlDimension(String.valueOf(dimensionRow.get("DimensionCode")));
        return dimension;
    }

    public Collection<OpenCodeEntry> getOpenCodeEntries(String tableCode) {
        return openCodeMultiMap.get(tableCode);
    }

    public Map<String, Boolean> getIsMetricNumericMap() {
        return Collections.unmodifiableMap(isMetricNumericMap);
    }

    public Map<String, Boolean> getIsDatapointNumericMap() {
        return Collections.unmodifiableMap(isDatapointNumericMap);
    }

    public Multimap<String, OpenCodeEntry> getOpenCodeMultiMap() {
        return openCodeMultiMap;
    }
}
