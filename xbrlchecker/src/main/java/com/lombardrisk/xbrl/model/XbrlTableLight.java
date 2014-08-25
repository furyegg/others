package com.lombardrisk.xbrl.model;

/**
 * Created by Bartosz Jedrzejewski on 10/06/14.
 */
public class XbrlTableLight {

    private String tableCode;
    private boolean isOpenY;
    private boolean isOpenZ;

    public XbrlTableLight(String tableCode, boolean isOpenY, boolean isOpenZ) {
        this.tableCode = tableCode;
        this.isOpenY = isOpenY;
        this.isOpenZ = isOpenZ;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public boolean isOpenY() {
        return isOpenY;
    }

    public void setOpenY(boolean isOpenY) {
        this.isOpenY = isOpenY;
    }

    public boolean isOpenZ() {
        return isOpenZ;
    }

    public void setOpenZ(boolean isOpenZ) {
        this.isOpenZ = isOpenZ;
    }
}
