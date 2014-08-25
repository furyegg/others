package com.lombardrisk.xbrl.render.xml;

import com.lombardrisk.xbrl.checker.csv.CsvUtil;
import com.lombardrisk.xbrl.model.XbrlDataPointLight;
import com.lombardrisk.xbrl.model.XbrlTable;
import com.lombardrisk.xbrl.model.XbrlTableLight;
import com.lombardrisk.xbrl.render.csv.DataPointCsvEntry;
import com.lombardrisk.xbrl.render.csv.DataPointCsvEntryDescriptor;
import com.lombardrisk.xbrl.render.ejb.XbrlModelService;
import com.lombardrisk.xbrl.render.util.ZipUtils;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bartosz Jedrzejewski on 09/06/14.
 */
public class XbrlParserToModelTest {

    public static final String XBRL_SAMPLE_XML = "xbrlSample.xml";


    @Test
    public void testBuildXbrlTablesAfterParsing() throws Exception {
        long timeStart = System.currentTimeMillis();

        //Creating XBRL service
        XbrlModelService xbrlModelService = new XbrlModelService();
        xbrlModelService.init();

        //Parsing of the XBRL Instance
        Reader xbrlReader = getXbrlReader(XBRL_SAMPLE_XML);
        XbrlParserToModel xbrlParserToModel = new XbrlParserToModel(xbrlModelService);
        XbrlInstanceParser xbrlInstanceParser = new XbrlInstanceParser(xbrlReader, xbrlParserToModel);
        xbrlInstanceParser.process();

        //Getting the light model
        List<XbrlTableLight> xbrlTableLights = xbrlParserToModel.buildXbrlTableLightsAfterParsing();
        List<XbrlDataPointLight> xbrlDataPointLights = xbrlParserToModel.buildXbrlDataPointLightsAfterParsing();

        System.out.println("XBRL tables parsed");
        long totalTime = System.currentTimeMillis() - timeStart;
        System.out.println("totalTime: "+totalTime/1000);
        System.out.println("Test Finished");
    }

    private Reader getXbrlReader(String fileName) throws IOException, URISyntaxException {
        final File file = getFile(fileName);
        final InputStream in = ZipUtils.getUncompressedInputStream(file);
        return new InputStreamReader(in);
    }

    private File getFile(String fileName) throws URISyntaxException {
        return new File(this.getClass().getClassLoader().getResource(fileName).toURI());
    }
}
