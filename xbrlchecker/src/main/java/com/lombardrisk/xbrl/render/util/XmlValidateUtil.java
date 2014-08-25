package com.lombardrisk.xbrl.render.util;

import com.sun.org.apache.xerces.internal.util.XMLCatalogResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * validate xml using external schema and the specified catalog file
 */
public abstract class XmlValidateUtil {

    /**
     *
     * @param catalogURLs the specified catalog url s e.g (jcr:/productConfig/ECR/schemas/catalog.xml)
     * @param xmlByteStream   the specified xml instance itself
     * @param schemaURLs    the specified schema url from jcr repository,also need to parsing the domain corresponding schema url
     * @param errorHandler  the sax error handler
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     *
     */
    public static void validate(String[] catalogURLs,InputStream xmlByteStream,String[]  schemaURLs,ErrorHandler errorHandler)
            throws SAXException, IOException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Source[] schemaSources = new Source[schemaURLs.length];
        int i= 0;
        for (String schemaURL : schemaURLs) {
            schemaSources[i++] = new StreamSource(new URL(null, schemaURL).toExternalForm());
        }
        schemaFactory.setResourceResolver(new XMLCatalogResolver(catalogURLs));
        Schema schema = schemaFactory.newSchema(schemaSources);
        factory.setSchema(schema);
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(errorHandler);
        reader.parse(new InputSource(xmlByteStream));
    }

    
}
