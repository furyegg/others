package com.lombardrisk.xbrl.model;

import java.util.*;

/**
 * Created by Bartosz Jedrzejewski on 06/06/14.
 */
public class XbrlTable {

    private String tableCode;
    private List<XbrlDataPoint> dataPoints;
    private XbrlDimension openAxisCriteria;
    private boolean openYAxis;

    public XbrlTable(String tableCode, Collection<XbrlDataPoint> dataPoints, XbrlDimension zAxisCriteria, boolean openYAxis) {
        this.tableCode = tableCode;
        this.dataPoints = new ArrayList<>(dataPoints);
        Collections.sort(this.dataPoints);
        this.openAxisCriteria = zAxisCriteria;
        this.openYAxis = openYAxis;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public List<XbrlDataPoint> getDataPoints() {
        return dataPoints;
    }

    public XbrlDimension getOpenAxisCriteria() {
        return openAxisCriteria;
    }

    public void setOpenAxisCriteria(XbrlDimension openAxisCriteria) {
        this.openAxisCriteria = openAxisCriteria;
    }

    public boolean isOpenYAxis() {
        return openYAxis;
    }

    public void setOpenYAxis(boolean openYAxis) {
        this.openYAxis = openYAxis;
    }
}
