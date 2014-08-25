package com.lombardrisk.xbrl.render.ejb;

import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.render.model.exception.XbrlRenderException;
import com.lombardrisk.xbrl.render.util.XmlValidateUtil;
import com.lombardrisk.xbrl.render.xml.XbrlUtils;
import com.lombardrisk.xbrl.render.xml.XmlConstants;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Cesar on 12/06/2014.
 */
@Stateless
public class XmlValidationBean {

    private static final Logger log = LoggerFactory.getLogger(XmlValidationBean.class);

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void validateXbrl(InputStream in) throws XbrlRenderException {
        try {
            final String taxonomyLocation = Config.INSTANCE.getString("xbrl.taxonomy.location");
            if (!new File(taxonomyLocation).exists()) {
                throw new IOException("Invalid path to taxonomy: " + taxonomyLocation);
            }

            final String catalogLocation = Config.INSTANCE.getString("xbrl.catalog.location");
            if (!new File(catalogLocation).exists()) {
                throw new IOException("Invalid path to taxonomy catalog: " + catalogLocation);
            }
            final String[] catalogFiles = new String[1];
            catalogFiles[0] = new File(catalogLocation).toURI().toString();

            final String root = new File(taxonomyLocation).toURI().toString();

            String entranceSchema;
            if(in.markSupported()) {
                log.debug("Input stream support marking. No need to make a copy");
                in.mark(Integer.MAX_VALUE);
                entranceSchema = XbrlUtils.getEntranceSchema(in);
                in.reset();
            }else {
                log.debug("Input stream does not support marking. I need to make a copy");
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                IOUtils.copy(in, out);
                final byte[] bytes = out.toByteArray();
                entranceSchema = XbrlUtils.getEntranceSchema(new ByteArrayInputStream(bytes));
                in = new ByteArrayInputStream(bytes);
            }
            entranceSchema = entranceSchema.replace("http://", root);
            entranceSchema = entranceSchema.replaceFirst(XmlConstants.XBRL_DATE_PATTERN, XmlConstants.EBA_TAXONOMY_DATE);
            log.debug("Entrance schema set to {}", entranceSchema);

            final String[] schemas = new String[2];
            schemas[0] = entranceSchema;
            schemas[1] = root + XmlConstants.XBRL_METRIC_XSD;

            final Set<String> errorSet = new LinkedHashSet<String>();

            final ErrorHandler errorHandler = new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    log.warn("XBRL instance validation warning: {}", exception.getMessage());
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    log.warn("XBRL instance validation error: {}", exception.getMessage());
                    errorSet.add(exception.getMessage());
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    log.error("XBRL instance validation fatal error: {}", exception.getMessage());
                    errorSet.add(exception.getMessage());
                }
            };
            XmlValidateUtil.validate(catalogFiles, in, schemas, errorHandler);
            in.close();
            if (!errorSet.isEmpty()) {
                throw new XbrlRenderException("Not a valid XBRL file");
            }
        } catch (XMLStreamException | IOException e) {
            throw new XbrlRenderException(e);
        } catch (SAXException | ParserConfigurationException e) {
            throw new XbrlRenderException("Could not validate xml", e);
        }
    }
}
