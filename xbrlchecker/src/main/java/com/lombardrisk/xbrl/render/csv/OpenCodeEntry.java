package com.lombardrisk.xbrl.render.csv;

/**
 * Created by Bartosz Jedrzejewski on 12/06/14.
 */
public class OpenCodeEntry {

    private String dimensionCode;
    private int tableOffset;

    public OpenCodeEntry() {
    }

    public OpenCodeEntry(String dimensionCode, int tableOffset) {
        this.dimensionCode = dimensionCode;
        this.tableOffset = tableOffset;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public int getTableOffset() {
        return tableOffset;
    }

    public void setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public void setTableOffset(int tableOffset) {
        this.tableOffset = tableOffset;
    }
}
