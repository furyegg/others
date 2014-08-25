package com.lombardrisk.xbrl.render.model.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cesar on 06/06/2014.
 */
public class XmlContext {
    private final String id;
    private final XmlIdentifier identifier;
    private final String period;
    private final List<XmlExplicitMember> scenario;

    private XmlContext(Builder builder) {
        this.identifier = builder.identifier;
        this.period = builder.period;
        this.scenario = builder.scenario;
        id = builder.id;
    }

    public String getId() {
        return id;
    }

    public XmlIdentifier getIdentifier() {
        return identifier;
    }

    public String getPeriod() {
        return period;
    }

    public List<XmlExplicitMember> getScenario() {
        return scenario;
    }

    @Override
    public String toString() {
        return "XmlContext{" +
                "id='" + id + '\'' +
                '}';
    }

    public static class Builder {
        private final String id;
        private XmlIdentifier identifier = null;
        private String period = null;
        private final List<XmlExplicitMember> scenario = new ArrayList<>();

        public Builder(String id) {
            this.id = id;
        }

        public Builder identifier(XmlIdentifier xmlIdentifier) {
            identifier = xmlIdentifier;
            return this;
        }

        public Builder period(String p) {
            period = p;
            return this;
        }

        public Builder addExplicitMember(XmlExplicitMember member) {
            scenario.add(member);
            return this;
        }

        public XmlContext build() {
            return new XmlContext(this);
        }
    }
}
