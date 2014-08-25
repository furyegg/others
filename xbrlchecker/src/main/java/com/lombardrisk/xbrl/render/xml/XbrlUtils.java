package com.lombardrisk.xbrl.render.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Cesar on 12/06/2014.
 */
public final class XbrlUtils {

    private XbrlUtils() {
    }


    /**
     * This methods returns the entrance schema which is at the beginning of the xbrl file. The input stream will be moved, so it is preferable to give a copy.
     *
     * @param in
     * @return
     * @throws XMLStreamException
     * @throws IOException
     */
    public static String getEntranceSchema(InputStream in) throws XMLStreamException, IOException {

        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = factory.createXMLStreamReader(in);
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (XmlConstants.XBRL_SCHEMA_REF.equals(reader.getLocalName())) {
                    final AttributeScanner scanner = new AttributeScanner(reader);
                    try {
                        return scanner.getAttribute(XmlConstants.HREF);
                    } finally {
                        reader.close();
                    }
                }
            }
        }
        return null;
    }
}
