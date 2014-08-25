package com.lombardrisk.xbrl.model;

/**
 * Created by Bartosz Jedrzejewski on 06/06/14.
 */
public class XbrlMetric {

    private String metricCode;

    public XbrlMetric(String metricCode) {
        this.metricCode = metricCode;
    }

    public String getMetricCode() {
        return metricCode;
    }

    public void setMetricCode(String metricCode) {
        this.metricCode = metricCode;
    }
}
