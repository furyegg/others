package com.lombardrisk.xbrl.render.csv;

/**
 * Created by Cesar on 12/06/2014.
 */
public class TableCodeOpenCodeEntry {
    private String tableCode;
    private OpenCodeEntry openCodeEntry;

    public TableCodeOpenCodeEntry() {
    }

    public TableCodeOpenCodeEntry(String tableCode, OpenCodeEntry openCodeEntry) {
        this.tableCode = tableCode;
        this.openCodeEntry = openCodeEntry;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public OpenCodeEntry getOpenCodeEntry() {
        return openCodeEntry;
    }

    public void setOpenCodeEntry(OpenCodeEntry openCodeEntry) {
        this.openCodeEntry = openCodeEntry;
    }
}
