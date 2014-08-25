package com.lombardrisk.xbrl.checker;

/**
 * Created by Cesar on 04/03/14.
 */
public final class EbaConstants {

    public static final String EBA_ROW_PARTICLE = "r";
    public static final String EBA_COLUMN_PARTICLE = "c";
    public static final String EBA_SHEET_PARTICLE = "s";
    public static final String EBA_ALL_ROWS = "rows";
    public static final String EBA_ALL_COLUMNS = "columns";
    public static final String EBA_ALL_SHEETS = "sheets";
    public static final String EBA_ALL = "All";

    public static final String DIMENSION_TABLE = "Dimension";
    public static final String CONTEXT_DEFINITION_TABLE = "ContextDefinition";
    public static final String CONTEXT_OF_DATAPOINTS_TABLE = "ContextOfDataPoints";
    public static final String MEMBER_TABLE = "Member";
    public static final String METRIC_TABLE = "Metric";
    public static final String DATAPOINT_VERSION_TABLE = "DataPointVersion";
    public static final String TABLE_VERSION_TABLE = "TableVersion";
    public static final String TABLE_VERSION_TABLE_CODE = "TableVersionCode";
    public static final String TABLE_VERSION_TABLE_LABEL = "TableVersionLabel";
    public static final String AXIS_TABLE = "Axis";
    public static final String AXIS_TABLE_ORIENTATION = "AxisOrientation";
    public static final String AXIS_TABLE_ORIENTATION_X = "X";
    public static final String AXIS_TABLE_ORIENTATION_Y = "Y";
    public static final String AXIS_TABLE_ORIENTATION_Z = "Z";
    public static final String AXIS_ORDINATE_TABLE = "AxisOrdinate";
    public static final String AXIS_ORDINATE_TABLE_CODE = "OrdinateCode";
    public static final String VALIDATION_RULE_TABLE = "ValidationRule";
    public static final String VALIDATION_RULE_TABLE_SCOPE = "Scope";
    public static final String VALIDATION_RULE_TABLE_VALIDATION_CODE = "ValidationCode";
    public static final String EXPRESSION_TABLE = "Expression";
    public static final String EXPRESSION_TABLE_FORMULA = "TableBasedFormula";
    public static final String EXPRESSION_TABLE_ID = "ExpressionID";
    public static final String TABLE_CELL_TABLE = "TableCell";
    public static final String CELL_POSITION_TABLE = "CellPosition";
    public static final String CELL_REF_PATTERN = "{0}r{1}c{2}s{3}";
    public static final String CELL_REF_PATTERN_NO_SHEET = "{0}r{1}c{2}";

    private EbaConstants(){
    }
}
