package com.lombardrisk.xbrl.render.model.xml;

/**
 * Created by Cesar on 06/06/2014.
 */
public class XmlExplicitMember {
    private final String dimension;
    private final String value;

    public XmlExplicitMember(String dimension, String value) {
        this.dimension = dimension;
        this.value = value;
    }

    public String getDimension() {
        return dimension;
    }

    public String getValue() {
        return value;
    }
}
