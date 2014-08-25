package com.lombardrisk.xbrl.model;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import java.util.List;

/**
 * Created by Bartosz Jedrzejewski on 10/06/14.
 */
public class XbrlDataPointLight{

    private String id;
    private String value;
    private String openCode;
    private List<XbrlTableLight> tableCodes;
    private final long numericId;
    private boolean isNumeric;

    public XbrlDataPointLight(String id, String value, String openCode, List<XbrlTableLight> tableCodes) {
        this.id = id;
        this.value = value;
        this.openCode = openCode;
        this.tableCodes = tableCodes;
        numericId = Long.parseLong(id);
    }

    public XbrlDataPointLight(long numericId, String openCode) {
        this.numericId = numericId;
        this.openCode = openCode;
        id = String.valueOf(numericId);
    }

    public long getNumericId() {
        return numericId;
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public void setNumeric(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOpenCode() {
        return openCode;
    }

    public void setOpenCode(String openCode) {
        this.openCode = openCode;
    }

    public List<XbrlTableLight> getTableCodes() {
        return tableCodes;
    }

    public void setTableCodes(List<XbrlTableLight> tableCodes) {
        this.tableCodes = tableCodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XbrlDataPointLight that = (XbrlDataPointLight) o;

        if (numericId != that.numericId) return false;
        if (openCode != null ? !openCode.equals(that.openCode) : that.openCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = openCode != null ? openCode.hashCode() : 0;
        result = 31 * result + (int) (numericId ^ (numericId >>> 32));
        return result;
    }

    public static Ordering<XbrlDataPointLight> getNaturalOrdering(){
        final Ordering<XbrlDataPointLight> dpmIdOrdering = Ordering.natural().onResultOf(new Function<XbrlDataPointLight, Long>() {
            @Override
            public Long apply(XbrlDataPointLight xbrlDataPointLight) {
                return xbrlDataPointLight.numericId;
            }
        });
        final Ordering<XbrlDataPointLight> openCodeOrdering = Ordering.natural().nullsLast().onResultOf(new Function<XbrlDataPointLight, String>() {
            @Override
            public String apply(XbrlDataPointLight xbrlDataPointLight) {
                return xbrlDataPointLight.openCode;
            }
        });
        return dpmIdOrdering.compound(openCodeOrdering);
    }

    @Override
    public String toString() {
        return "XbrlDataPointLight{" +
                "id='" + id + '\'' +
                ", openCode='" + openCode + '\'' +
                '}';
    }
}
