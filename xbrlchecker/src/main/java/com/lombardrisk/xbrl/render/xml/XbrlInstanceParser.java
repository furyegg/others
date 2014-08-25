package com.lombardrisk.xbrl.render.xml;

import com.lombardrisk.xbrl.render.model.xml.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cesar on 06/06/2014.
 */
public class XbrlInstanceParser {
    private static final Logger log = LoggerFactory.getLogger(XbrlInstanceParser.class);

    private final XMLStreamReader reader;
    private final XbrlParserListener listener;
    private final Map<String, XmlContext> contextMap = new HashMap<>();
    private final Map<String, XmlUnit> unitMap = new HashMap<>();

    public XbrlInstanceParser(Reader reader, XbrlParserListener listener) throws XMLStreamException {
        this.listener = listener;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        this.reader = factory.createXMLStreamReader(reader);
    }

    public void process() throws XMLStreamException, XbrlIntegrityException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
               if (XmlConstants.SCHEMA_REF.equals(reader.getLocalName())) {
                    checkSchemaRef();
                } else if (XmlConstants.XBRLI_UNIT.equals(reader.getLocalName())) {
                    processUnit();
                } else if (XmlConstants.XBRL_CONTEXT.equals(reader.getLocalName())) {
                    processContext();
                } else if (XmlConstants.XBRL_FILING_INDICATORS.equals(reader.getLocalName())) {
                    processFilingIndicators();
                } else if (XmlConstants.EBA_METRIC_NS.equals(reader.getNamespaceURI())) {
                    processMetric();
                }
            }
        }
    }

    private void processMetric() throws XbrlIntegrityException, XMLStreamException {
        final String metricId = reader.getLocalName();
        final AttributeScanner scanner = new AttributeScanner(reader);
        final String contextId = scanner.getAttribute(XmlConstants.XBRL_METRIC_CONTEXT_REF);
        final XmlContext xmlContext = getXmlContext(contextId);
        final XmlMetric.Builder metricBuilder = new XmlMetric.Builder(xmlContext, metricId);

        final String decimals = scanner.getAttribute(XmlConstants.XBRL_METRIC_DECIMALS);
        if (!StringUtils.isEmpty(decimals)) {
            try {
                metricBuilder.decimal(Integer.parseInt(decimals));
            } catch (NumberFormatException e) {
                metricBuilder.decimal(0);
            }
        }

        final String unitId = scanner.getAttribute(XmlConstants.XBRL_METRIC_UNIT_REF);
        if (!StringUtils.isEmpty(unitId)) {
            final XmlUnit xmlUnit = getXmlUnit(unitId);
            metricBuilder.xbrlUnit(xmlUnit);
        }

        metricBuilder.value(reader.getElementText());
        final XmlMetric xmlMetric = metricBuilder.build();
        listener.metric(xmlMetric);
    }

    private void processFilingIndicators() throws XMLStreamException, XbrlIntegrityException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (XmlConstants.XBRL_FILING_INDICATOR.equals(reader.getLocalName())) {
                    final AttributeScanner scanner = new AttributeScanner(reader);
                    final String contextId = scanner.getAttribute(XmlConstants.XBRL_FILING_INDICATOR_CONTEXT_REF);
                    final String value = reader.getElementText();
                    final XmlContext xmlContext = getXmlContext(contextId);
                    final XmlFilingIndicator filingIndicator = new XmlFilingIndicator(xmlContext, value);
                    listener.filingIndicator(filingIndicator);
                }
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (XmlConstants.XBRL_FILING_INDICATORS.equals(reader.getLocalName())) {
                    return;
                }
            }
        }
    }

    private void processContext() throws XMLStreamException {
        final AttributeScanner attributeScanner = new AttributeScanner(reader);
        final String id = attributeScanner.getAttribute(XmlConstants.XBRL_CONTEXT_ID);
        final XmlContext.Builder contextBuilder = new XmlContext.Builder(id);
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (XmlConstants.XBRL_ENTITY.equals(reader.getLocalName())) {
                    processEntity(contextBuilder);
                } else if (XmlConstants.XBRL_PERIOD.equals(reader.getLocalName())) {
                    processInstant(contextBuilder);
                } else if (XmlConstants.XBRL_SCENARIO.equals(reader.getLocalName())) {
                    processScenario(contextBuilder);
                }
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (XmlConstants.XBRL_CONTEXT.equals(reader.getLocalName())) {
                    final XmlContext context = contextBuilder.build();
                    contextMap.put(context.getId(), context);
                    listener.context(context);
                    return;
                }
            }
        }
    }

    private void processScenario(XmlContext.Builder contextBuilder) throws XMLStreamException {
        String dimension = null;
        String memberValue = null;
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (XmlConstants.XBRL_EXPLICIT_MEMBER.equals(reader.getLocalName())) {
                    final AttributeScanner scanner = new AttributeScanner(reader);
                    dimension = scanner.getAttribute(XmlConstants.XBRL_SCENARIO_DIMENSION);
                    memberValue = reader.getElementText();
                    final XmlExplicitMember member = new XmlExplicitMember(dimension, memberValue);
                    contextBuilder.addExplicitMember(member);
                }else if(XmlConstants.XBRL_TYPE_MEMBER.equals(reader.getLocalName())){
                    final AttributeScanner scanner = new AttributeScanner(reader);
                    dimension = scanner.getAttribute(XmlConstants.XBRL_SCENARIO_DIMENSION);
                    processExplicitType(contextBuilder, dimension);
                }
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (XmlConstants.XBRL_SCENARIO.equals(reader.getLocalName())) {
                    return;
                }
            }
        }
    }

    private void processExplicitType(XmlContext.Builder contextBuilder, String dimension) throws XMLStreamException {
        String part1 = null;
        String value = null;
        while(reader.hasNext()){
            reader.next();
            if(reader.getEventType() == XMLStreamConstants.START_ELEMENT){
                if(XmlConstants.EBA_TYPE.equals(reader.getNamespaceURI())){
                    part1 = reader.getLocalName();
                    value = reader.getElementText();
                }
            }else if(reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (XmlConstants.XBRL_TYPE_MEMBER.equals(reader.getLocalName())) {
                    final String memberValue = part1 + XmlConstants.XBRL_MEMBER_SEPARATOR +value;
                    final XmlExplicitMember member = new XmlExplicitMember(dimension, memberValue);
                    contextBuilder.addExplicitMember(member);
                    return;
                }
            }
        }
    }

    private void processInstant(XmlContext.Builder contextBuilder) throws XMLStreamException {
        String instant = null;
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (XmlConstants.XBRL_INSTANT.equals(reader.getLocalName())) {
                    instant = reader.getElementText();
                }
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (XmlConstants.XBRL_PERIOD.equals(reader.getLocalName())) {
                    contextBuilder.period(instant);
                    return;
                }
            }
        }
    }

    private void processEntity(XmlContext.Builder contextBuilder) throws XMLStreamException {
        String scheme = null;
        String identifier = null;
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (XmlConstants.XBRL_IDENTIFIER.equals(reader.getLocalName())) {
                    final AttributeScanner attributeScanner = new AttributeScanner(reader);
                    scheme = attributeScanner.getAttribute(XmlConstants.XBRL_IDENTIFIER_SCHEME);
                    identifier = reader.getElementText();
                }
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (XmlConstants.XBRL_ENTITY.equals(reader.getLocalName())) {
                    final XmlIdentifier xmlIdentifier = new XmlIdentifier(scheme, identifier);
                    contextBuilder.identifier(xmlIdentifier);
                    return;
                }
            }
        }
    }

    private void processUnit() throws XMLStreamException {
        final AttributeScanner attributeScanner = new AttributeScanner(reader);
        final String id = attributeScanner.getAttribute(XmlConstants.XBRL_UNIT_ID);
        String measure = null;
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (XmlConstants.XBRL_MEASURE.equals(reader.getLocalName())) {
                    measure = reader.getElementText();
                }
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (XmlConstants.XBRLI_UNIT.equals(reader.getLocalName())) {
                    final XmlUnit xmlUnit = new XmlUnit(id, measure);
                    unitMap.put(xmlUnit.getId(), xmlUnit);
                    listener.unit(xmlUnit);
                    return;
                }
            }
        }
    }

    private void checkSchemaRef() throws XbrlIntegrityException {
        final AttributeScanner attributeScanner = new AttributeScanner(reader);
        final String href = attributeScanner.getAttribute(XmlConstants.HREF);
        if (!href.contains(XmlConstants.EBA_REF)) {
            throw new XbrlIntegrityException(MessageFormat.format("Schema ref {0} is not supported.", href));
        }
        log.info(MessageFormat.format("''{0}'' is a valid schemaRef. I will carry on processing", href));
    }

    private XmlContext getXmlContext(String contextId) throws XbrlIntegrityException {
        final XmlContext xmlContext = contextMap.get(contextId);
        if (xmlContext == null) {
            throw new XbrlIntegrityException(MessageFormat.format("Unknown context reference ''{0}''", contextId));
        }
        return xmlContext;
    }

    private XmlUnit getXmlUnit(String unitId) throws XbrlIntegrityException {
        final XmlUnit xmlUnit = unitMap.get(unitId);
        if (xmlUnit == null) {
            throw new XbrlIntegrityException(MessageFormat.format("Unknown unit reference ''{0}''", unitId));
        }
        return xmlUnit;
    }
}
