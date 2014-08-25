package com.lombardrisk.xbrl.model;

/**
 * Created by Bartosz Jedrzejewski on 06/06/14.
 */
public class XbrlDimensionMember {

    private XbrlDimension dimension;
    private String value;

    public XbrlDimensionMember(XbrlDimension dimension, String value) {
        this.dimension = dimension;
        this.value = value;
    }

    public XbrlDimension getDimension() {
        return dimension;
    }

    public void setDimension(XbrlDimension dimension) {
        this.dimension = dimension;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
