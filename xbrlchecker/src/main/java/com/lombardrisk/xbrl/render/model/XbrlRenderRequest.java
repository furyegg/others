package com.lombardrisk.xbrl.render.model;

import com.lombardrisk.xbrl.model.XbrlDataPointLight;
import com.lombardrisk.xbrl.model.XbrlTableLight;

import java.util.*;

/**
 * Created by Cesar on 09/06/2014.
 */
public class XbrlRenderRequest {

    private final Collection<XbrlTableLight> xbrlTableLights = new ArrayList<>();
    private final List<XbrlDataPointLight> xbrlDataPointLights = new ArrayList<>();
    private final String xbrlFileName;
    private final Calendar requestTime;
    private final Map<String, Boolean> dataPointNumericTypeMap = new HashMap<>();

    public XbrlRenderRequest(Collection<XbrlTableLight> xbrlTableLights, List<XbrlDataPointLight> xbrlDataPointLights, String xbrlFileName) {
        this.xbrlTableLights.addAll(xbrlTableLights);
        this.xbrlDataPointLights.addAll(xbrlDataPointLights);
        Collections.sort(this.xbrlDataPointLights, XbrlDataPointLight.getNaturalOrdering());
        this.xbrlFileName = xbrlFileName;
        requestTime = Calendar.getInstance();
        for (XbrlTableLight table : this.xbrlTableLights) {
            table.setTableCode(table.getTableCode().replace("_", " "));
        }
    }

    public void putAllDataPointNumericType(Map<String, Boolean> newMap){
        dataPointNumericTypeMap.putAll(newMap);
    }

    public Map<String, Boolean> getDataPointNumericTypeMap() {
        return Collections.unmodifiableMap(dataPointNumericTypeMap);
    }

    public Collection<XbrlTableLight> getXbrlTableLights() {
        return xbrlTableLights;
    }

    public List<XbrlDataPointLight> getXbrlDataPointLights() {
        return xbrlDataPointLights;
    }

    public String getXbrlFileName() {
        return xbrlFileName;
    }

    public Calendar getRequestTime() {
        return requestTime;
    }
}
