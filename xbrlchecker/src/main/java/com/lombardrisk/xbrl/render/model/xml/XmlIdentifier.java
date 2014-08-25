package com.lombardrisk.xbrl.render.model.xml;

/**
 * Created by Cesar on 06/06/2014.
 */
public class XmlIdentifier {
    private final String scheme;
    private final String identifier;

    public XmlIdentifier(String scheme, String identifier) {
        this.scheme = scheme;
        this.identifier = identifier;
    }

    public String getScheme() {
        return scheme;
    }

    public String getIdentifier() {
        return identifier;
    }
}
