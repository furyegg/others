package com.lombardrisk.xbrl.render.xml;

import javax.xml.stream.XMLStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cesar on 12/02/14.
 */
public class AttributeScanner {
    private final Map<String, Integer> m;
    private final XMLStreamReader reader;

    public AttributeScanner(XMLStreamReader reader) {
        this.reader = reader;
        m = buildAttributeMap(reader);
    }

    public String getAttribute(String name) {
        Integer i = m.get(name);
        return i != null ? reader.getAttributeValue(m.get(name)) : "";
    }

    public Collection<String> getAttributeNames() {
        return Collections.unmodifiableCollection(m.keySet());
    }

    private static Map<String, Integer> buildAttributeMap(XMLStreamReader reader) {
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            m.put(reader.getAttributeLocalName(i), i);
        }
        return m;
    }
}
