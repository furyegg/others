package com.lombardrisk.xbrl.render.xml;

import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.model.*;
import com.lombardrisk.xbrl.render.csv.OpenCodeEntry;
import com.lombardrisk.xbrl.render.ejb.XbrlModelService;
import com.lombardrisk.xbrl.render.model.XbrlRenderListener;
import com.lombardrisk.xbrl.render.model.xml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bartosz Jedrzejewski on 09/06/14.
 */
public class XbrlParserToModel implements XbrlParserListener {
    private static final Logger log = LoggerFactory.getLogger(XbrlParserToModel.class);
    private List<XmlFilingIndicator> xmlFilingIndicators = new ArrayList<>();
    private List<XmlMetric> xmlMetrics = new ArrayList<>();
    private XbrlModelService xbrlModelService;
    private Map<String, XbrlTableLight> xbrlTableLightMap = new HashMap<>();

    public XbrlParserToModel(XbrlModelService xbrlModelService) {
        this.xbrlModelService = xbrlModelService;
    }

    public List<XbrlTableLight> buildXbrlTableLightsAfterParsing() throws IOException {
        List<XbrlTableLight> xbrlTableLights = new ArrayList<>();
        for (XmlFilingIndicator indicator : xmlFilingIndicators) {
            xbrlTableLights.addAll(xbrlModelService.creatXbrlTableLights(indicator.getValue()));
        }
        for (XbrlTableLight tableLight : xbrlTableLights) {
            xbrlTableLightMap.put(tableLight.getTableCode(), tableLight);
        }
        return xbrlTableLights;
    }

    public List<XbrlDataPointLight> buildXbrlDataPointLightsAfterParsing() throws IOException {
        return buildXbrlDataPointLightsAfterParsing(null);
    }

    public List<XbrlDataPointLight> buildXbrlDataPointLightsAfterParsing(XbrlRenderListener listener) throws IOException {
        if(xbrlTableLightMap.isEmpty()){
            buildXbrlTableLightsAfterParsing();
        }
        List<XbrlDataPointLight> dataPointLights = new ArrayList<>();
        final Splitter splitter = Splitter.on(XmlConstants.XBRL_MEMBER_SEPARATOR);
        final int total = xmlMetrics.size();
        final int step = Config.INSTANCE.getInt("xblr.render.process.datapoints.step");
        int done = 0;
        int lastDone = 0;
        long lastTime = 0;
        final long start = System.currentTimeMillis();
        final Multimap<String, OpenCodeEntry> openCodeMultiMap = xbrlModelService.getOpenCodeMultiMap();
        final Map<String, List<String>> dataPointsMap = xbrlModelService.getDataPointsMap();
        for (XmlMetric metric : xmlMetrics) {
            List<XbrlDimensionMember> xbrlDimensionMembers = new ArrayList<>();
            for(XmlExplicitMember member : metric.getXmlContext().getScenario()){
                XbrlDimension dimension = new XbrlDimension(splitter.splitToList(member.getDimension()).get(1));
                XbrlDimensionMember dimensionMember = new XbrlDimensionMember(dimension , splitter.splitToList(member.getValue()).get(1));
                xbrlDimensionMembers.add(dimensionMember);
            }
            XbrlMetric xbrlMetric = new XbrlMetric(metric.getId());
            String key = XbrlModelUtils.createDatapointKey(xbrlDimensionMembers, xbrlMetric);
            List<String> dpNameAndTables =  dataPointsMap.get(key);
            if(dpNameAndTables == null) {
                log.warn("Could not find data point for {}", key);
                continue;
            }
            List<XbrlTableLight> xbrlTableLights = new ArrayList<>();
            XbrlTableLight tableLight;
            for (String dpNameAndTable : dpNameAndTables) {
                tableLight = xbrlTableLightMap.get(dpNameAndTable);
                if (tableLight != null) {
                    xbrlTableLights.add(tableLight);
                }
            }

            String openCode = XbrlModelUtils.checkForOpenCode(xbrlDimensionMembers, xbrlTableLights, openCodeMultiMap);
            XbrlDataPointLight dataPointLight = new XbrlDataPointLight(dpNameAndTables.get(0), metric.getValue(),openCode,xbrlTableLights);
            dataPointLight.setNumeric(xbrlModelService.getIsMetricNumericMap().get(metric.getId()));
            dataPointLights.add(dataPointLight);
            done++;
            if (done % step == 0) {
                log.info(MessageFormat.format("Done {0}/{1} data points", done, total));
                if (listener != null) {
                    listener.processingDataPoints(done, total);
                }
//                if(lastTime != 0) {
//                    double rate = (double) (done - lastDone) * TimeUnit.SECONDS.toMillis(1) / (System.currentTimeMillis() - lastTime);
//                    Runtime runtime = Runtime.getRuntime();
//                    log.info(MessageFormat.format("{0} dp/s. Free Memory: {1} MB", rate, runtime.freeMemory() / (1024* 1024)));
//                }
//                lastTime = System.currentTimeMillis();
//                lastDone = done;
            }
        }
        log.info(MessageFormat.format("Total time {0} ms", System.currentTimeMillis() - start));
        if(listener != null) {
            listener.processedAllDataPoints();
        }
        return dataPointLights;
    }

    @Override
    public void unit(XmlUnit xmlUnit) {
        //Does not need to do anything
    }

    @Override
    public void context(XmlContext xmlContext) {
        //Does not need to do anything
    }

    @Override
    public void filingIndicator(XmlFilingIndicator xmlFilingIndicator) {
        xmlFilingIndicators.add(xmlFilingIndicator);
    }

    @Override
    public void metric(XmlMetric xmlMetric) {
        xmlMetrics.add(xmlMetric);
    }
}
