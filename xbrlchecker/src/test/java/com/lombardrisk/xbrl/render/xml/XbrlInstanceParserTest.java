package com.lombardrisk.xbrl.render.xml;

import com.lombardrisk.xbrl.render.model.xml.XmlContext;
import com.lombardrisk.xbrl.render.model.xml.XmlFilingIndicator;
import com.lombardrisk.xbrl.render.model.xml.XmlMetric;
import com.lombardrisk.xbrl.render.model.xml.XmlUnit;
import com.lombardrisk.xbrl.render.util.ZipUtils;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.URISyntaxException;
import java.text.MessageFormat;

public class XbrlInstanceParserTest {

    public static final String XBRL_SAMPLE_XML = "xbrlSample.xml";
    public static final String XBRL_SAMPLE_XML_GZIP = "xbrlSample.xml.gz";
    public static final String XBRL_SAMPLE_XML_ZIP = "xbrlSample.zip";

    @Test
    public void testUnzip() throws Exception {
        Reader reader = getXbrlReader(XBRL_SAMPLE_XML);
        XbrlInstanceParser parser = createXbrlInstanceParser(reader);
        parser.process();
    }

    @Test
    public void testGzip() throws Exception {
        Reader reader = getXbrlReader(XBRL_SAMPLE_XML_GZIP);
        XbrlInstanceParser parser = createXbrlInstanceParser(reader);
        parser.process();
    }

    @Test
    public void testZip() throws Exception {
        Reader reader = getXbrlReader(XBRL_SAMPLE_XML_ZIP);
        XbrlInstanceParser parser = createXbrlInstanceParser(reader);
        parser.process();
    }

    private static XbrlInstanceParser createXbrlInstanceParser(Reader reader) throws XMLStreamException {
        return new XbrlInstanceParser(reader, new XbrlParserListener() {
            @Override
            public void unit(XmlUnit xmlUnit) {
                System.out.println(MessageFormat.format("New unit {0}", xmlUnit));
            }

            @Override
            public void context(XmlContext xmlContext) {
                System.out.println(MessageFormat.format("New context {0}", xmlContext));
            }

            @Override
            public void filingIndicator(XmlFilingIndicator xmlFilingIndicator) {
                System.out.println(MessageFormat.format("New filing indicator {0}", xmlFilingIndicator));
            }

            @Override
            public void metric(XmlMetric xmlMetric) {
                System.out.println(MessageFormat.format("New metric {0}", xmlMetric));
            }
        });
    }

    private File getFile(String fileName) throws URISyntaxException {
        return new File(this.getClass().getClassLoader().getResource(fileName).toURI());
    }

    private Reader getXbrlReader(String fileName) throws IOException, URISyntaxException {
        final File file = getFile(fileName);
        final InputStream in = ZipUtils.getUncompressedInputStream(file);
        return new InputStreamReader(in);
    }

}