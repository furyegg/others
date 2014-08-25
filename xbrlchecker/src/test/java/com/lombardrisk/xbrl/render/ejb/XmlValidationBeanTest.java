package com.lombardrisk.xbrl.render.ejb;

import com.lombardrisk.xbrl.render.util.ZipUtils;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

public class XmlValidationBeanTest {

    public static final String XBRL_FILE = "xbrlSample.xml";
    public static final String XBRL_FILE_ZIPPED = "xbrlSample.xml.gz";

    @Test
    public void testValidateXbrl() throws Exception {
        File xbrl = new File(this.getClass().getClassLoader().getResource(XBRL_FILE).toURI());
        InputStream in2 = ZipUtils.getUncompressedInputStream(xbrl);

        XmlValidationBean bean = new XmlValidationBean();
        bean.validateXbrl(in2);
    }

    @Test
    public void testValidateXbrlZipped() throws Exception {
        File xbrl = new File(this.getClass().getClassLoader().getResource(XBRL_FILE_ZIPPED).toURI());
        InputStream in2 = ZipUtils.getUncompressedInputStream(xbrl);

        XmlValidationBean bean = new XmlValidationBean();
        bean.validateXbrl(in2);
    }
}