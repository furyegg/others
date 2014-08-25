package com.lombardrisk.xbrl.render.ejb;

import com.lombardrisk.xbrl.model.XbrlDataPointLight;
import com.lombardrisk.xbrl.model.XbrlTableLight;
import com.lombardrisk.xbrl.render.model.XbrlRenderListener;
import com.lombardrisk.xbrl.render.model.XbrlRenderRequest;
import com.lombardrisk.xbrl.render.model.exception.UserTokenException;
import com.lombardrisk.xbrl.render.model.exception.XbrlRenderException;
import com.lombardrisk.xbrl.render.xml.XbrlInstanceParser;
import com.lombardrisk.xbrl.render.xml.XbrlIntegrityException;
import com.lombardrisk.xbrl.render.xml.XbrlParserToModel;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Cesar on 10/06/2014.
 */
@Stateless
public class XbrlRenderService {

    private static final Logger log = LoggerFactory.getLogger(XbrlRenderService.class);

    @Inject
    private UserTokenDao userTokenDao;

    @Inject
    private ExcelRenderService excelRenderService;

    @Inject
    private XbrlModelService xbrlModelService;

    @Inject
    private XmlValidationBean validationBean;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void test(String token) throws UserTokenException, XbrlRenderException {
        userTokenDao.consumeUserToken(token);
        throw new XbrlRenderException("Error happened");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Workbook uploadXbrl(InputStream xbrlContent, String xbrlFileName, XbrlRenderListener listener) throws XbrlRenderException {
        log.info("Request to render file {}", xbrlFileName);
        try {
            if (listener != null) {
                listener.validatingInstance();
            }
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(xbrlContent, out);
            //first validation of the XBRL file
            final byte[] bytes = out.toByteArray();
            xbrlContent.close();
            out.close();

            log.info("Validating xbrl file {}", xbrlFileName);
            validationBean.validateXbrl(new BufferedInputStream(new ByteArrayInputStream(bytes)));
            log.info("Validation passed for xbrl file {}", xbrlFileName);
            if (listener != null) {
                listener.instanceValidated();
                listener.readingInstance();
            }
            log.info("Reading xbrl instance for xbrl file {}", xbrlFileName);
            final Reader xbrlReader = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(bytes)));
            final XbrlParserToModel xbrlParserToModel = new XbrlParserToModel(xbrlModelService);
            XbrlInstanceParser xbrlInstanceParser = new XbrlInstanceParser(xbrlReader, xbrlParserToModel);
            xbrlInstanceParser.process();
            xbrlReader.close();
            log.info("Finish reading file");
            if (listener != null) {
                listener.instanceRead();
            }
            //Getting the light model
            final List<XbrlTableLight> tables = xbrlParserToModel.buildXbrlTableLightsAfterParsing();
            log.info("Finish building tables");
            final List<XbrlDataPointLight> dataPoints = xbrlParserToModel.buildXbrlDataPointLightsAfterParsing(listener);
            log.info("Finish building data points");
            final Map<String, Boolean> isDataPointNumericMap = xbrlModelService.getIsDatapointNumericMap();
            final XbrlRenderRequest xbrlRenderRequest = new XbrlRenderRequest(tables, dataPoints, xbrlFileName);
            xbrlRenderRequest.putAllDataPointNumericType(isDataPointNumericMap);
            log.info("Finished reading xbrl instance for xbrl file {}", xbrlFileName);
            if(listener != null){
                listener.rendering();
            }
            log.info("Preparing Excel file for xbrl file {}", xbrlFileName);
            final Workbook wb = excelRenderService.getExcelRenderedXbrl(xbrlRenderRequest);
            log.info("Finished preparing Excel file for xbrl file {}", xbrlFileName);
            if(listener != null){
                listener.finishedRendering();
            }
            return wb;
        } catch (XMLStreamException | XbrlIntegrityException | IOException e) {
            throw new XbrlRenderException(e);
        }
    }
}
