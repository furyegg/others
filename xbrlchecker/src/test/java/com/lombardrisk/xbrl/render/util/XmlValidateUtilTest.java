package com.lombardrisk.xbrl.render.util;

import com.lombardrisk.xbrl.render.xml.XbrlUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

public class XmlValidateUtilTest {

    private static final Logger log = LoggerFactory.getLogger(XmlValidateUtilTest.class);

    @Ignore
    @Test
    public void testValidate() throws Exception {
        File f = new File("D:\\DataShare\\xbrlTaxonomy\\catalog.xml");
        String root = new File("D:\\DataShare\\xbrlTaxonomy").toURI().toString();
        String[] catalogFiles = new String[]{f.toURI().toString()};
        System.out.println(root);
        String[] schemas = new String[2];
        schemas[0] = root +
                "www.eba.europa.eu/eu/fr/xbrl/crr/fws/corep/its-2013-02/2013-12-01/mod/corep_lcr_ind.xsd";
        schemas[0] = schemas[0].replaceFirst("[\\d]{4}-[\\d]{2}-[\\d]{2}", "2013-12-01");
        System.out.println(schemas[0]);
        schemas[1] = root +
                "www.eba.europa.eu/eu/fr/xbrl/crr/dict/met/met.xsd";

        File xbrl = new File(this.getClass().getClassLoader().getResource("xbrlSample.xml").toURI());
        InputStream in = ZipUtils.getUncompressedInputStream(xbrl);

//        XbrlUtils.getEntranceSchema(in);

                final Set<String> warningSet = new LinkedHashSet<String>();
        final Set<String> errorSet = new LinkedHashSet<String>();
        final Set<String> fatalErrorSet = new LinkedHashSet<String>();

        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                log.warn("XBRL instance validation warning: {}", exception.getMessage());
                warningSet.add(exception.getMessage());
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                log.warn("XBRL instance validation error: {}", exception.getMessage());
                errorSet.add(exception.getMessage());
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                log.error("XBRL instance validation fatal error: {}", exception.getMessage());
                fatalErrorSet.add(exception.getMessage());
            }
        };

        XmlValidateUtil.validate(catalogFiles, in, schemas, errorHandler);

    }
}