package com.lombardrisk.xbrl.model;

/**
 * Created by Bartosz Jedrzejewski on 06/06/14.
 */
public class XbrlDataPoint implements Comparable<XbrlDataPoint>{

    private String dataPointID;
    private String dataPointValue;
    private XbrlContext dataPointContext;
    private XbrlMetric dataPointMetric;
    private final long dataPointIdLong;

    public XbrlDataPoint(String dataPointID, XbrlContext dataPointContext, XbrlMetric dataPointMetric) {
        this.dataPointID = dataPointID;
        this.dataPointContext = dataPointContext;
        this.dataPointMetric = dataPointMetric;
        dataPointIdLong = Long.parseLong(dataPointID);
    }

    public XbrlDataPoint(long dataPointIdLong) {
        this.dataPointIdLong = dataPointIdLong;
        dataPointID = String.valueOf(dataPointIdLong);
    }

    public String getDataPointID() {
        return dataPointID;
    }

    public void setDataPointID(String dataPointID) {
        this.dataPointID = dataPointID;
    }

    public String getDataPointValue() {
        return dataPointValue;
    }

    public void setDataPointValue(String dataPointValue) {
        this.dataPointValue = dataPointValue;
    }

    public XbrlContext getDataPointContext() {
        return dataPointContext;
    }

    public void setDataPointContext(XbrlContext dataPointContext) {
        this.dataPointContext = dataPointContext;
    }

    public XbrlMetric getDataPointMetric() {
        return dataPointMetric;
    }

    public void setDataPointMetric(XbrlMetric dataPointMetric) {
        this.dataPointMetric = dataPointMetric;
    }

    @Override
    public int compareTo(XbrlDataPoint o) {
        return (int) (dataPointIdLong - o.dataPointIdLong);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XbrlDataPoint that = (XbrlDataPoint) o;

        if (dataPointIdLong != that.dataPointIdLong) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (dataPointIdLong ^ (dataPointIdLong >>> 32));
    }
}
