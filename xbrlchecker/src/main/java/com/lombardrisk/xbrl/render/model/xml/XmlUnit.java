package com.lombardrisk.xbrl.render.model.xml;

/**
 * Created by Cesar on 06/06/2014.
 */
public class XmlUnit {
    private final String id;
    private final String measure;

    public XmlUnit(String id, String measure) {
        this.id = id;
        this.measure = measure;
    }

    public String getId() {
        return id;
    }

    public String getMeasure() {
        return measure;
    }

    @Override
    public String toString() {
        return "XmlUnit{" +
                "id='" + id + '\'' +
                ", measure='" + measure + '\'' +
                '}';
    }
}
