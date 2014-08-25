package com.lombardrisk.xbrl.render.model.xml;

/**
 * Created by Cesar on 06/06/2014.
 */
public class XmlMetric {
    private final String id;
    private final XmlContext xmlContext;
    private final XmlUnit xmlUnit;
    private final int decimal;
    private final String value;

    private XmlMetric(Builder builder) {
        this.id = builder.id;
        this.xmlContext = builder.xmlContext;
        this.xmlUnit = builder.xmlUnit;
        this.decimal = builder.decimal;
        this.value = builder.value;
    }

    public String getId() {
        return id;
    }

    public XmlContext getXmlContext() {
        return xmlContext;
    }

    public XmlUnit getXmlUnit() {
        return xmlUnit;
    }

    public int getDecimal() {
        return decimal;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "XmlMetric{" +
                "id='" + id + '\'' +
                ", xmlContext=" + xmlContext.getId() +
                ", value='" + value + '\'' +
                '}';
    }

    public static class Builder{
        private final XmlContext xmlContext;
        private final String id;
        private XmlUnit xmlUnit = null;
        private int decimal = 0;
        private String value = null;

        public Builder(XmlContext xmlContext, String id) {
            this.xmlContext = xmlContext;
            this.id = id;
        }

        public Builder xbrlUnit(XmlUnit unit){
            xmlUnit = unit;
            return this;
        }

        public Builder decimal(int dec){
            decimal = dec;
            return this;
        }

        public Builder value(String val){
            value = val;
            return this;
        }

        public XmlMetric build() {
            return new XmlMetric(this);
        }
    }
}
