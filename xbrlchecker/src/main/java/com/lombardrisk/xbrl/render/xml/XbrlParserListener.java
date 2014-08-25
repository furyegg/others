package com.lombardrisk.xbrl.render.xml;

import com.lombardrisk.xbrl.render.model.xml.XmlContext;
import com.lombardrisk.xbrl.render.model.xml.XmlFilingIndicator;
import com.lombardrisk.xbrl.render.model.xml.XmlMetric;
import com.lombardrisk.xbrl.render.model.xml.XmlUnit;

/**
 * Created by Cesar on 06/06/2014.
 */
public interface XbrlParserListener {
    void unit(XmlUnit xmlUnit);

    void context(XmlContext xmlContext);

    void filingIndicator(XmlFilingIndicator xmlFilingIndicator);

    void metric(XmlMetric xmlMetric);
}
