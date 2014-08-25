package com.lombardrisk.xbrl.model;

/**
 * Created by Bartosz Jedrzejewski on 06/06/14.
 */
public class XbrlDimension {

    private String dimensionCode;

    public XbrlDimension(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }
}
