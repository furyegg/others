package com.lombardrisk.xbrl.render.model.xml;

/**
 * Created by Cesar on 06/06/2014.
 */
public class XmlFilingIndicator {
    private final XmlContext xmlContext;
    private final String value;

    public XmlFilingIndicator(XmlContext xmlContext, String value) {
        this.xmlContext = xmlContext;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public XmlContext getXmlContext() {
        return xmlContext;
    }

    @Override
    public String toString() {
        return "XmlFilingIndicator{" +
                "xmlContext=" + xmlContext.getId() +
                ", value='" + value + '\'' +
                '}';
    }
}
