package com.lombardrisk.xbrl.model;

import com.google.common.collect.Multimap;
import com.lombardrisk.xbrl.render.csv.OpenCodeEntry;

import java.util.*;

/**
 * Created by Bartosz Jedrzejewski on 11/06/14.
 */
public final class XbrlModelUtils {

    private XbrlModelUtils(){
    }

    public static final String XBRL_OPEN_DELIMETER = "$|$";
    public static final String XBRL_OPEN_DELIMITER_OPEN_BOUNDARY = "(";
    public static final String XBRL_OPEN_DELIMITER_CLOSE_BOUNDARY = ")";

    private static Set<String> openDimensions = new HashSet<String>() {{
        add("CEG");
        add("LEC");
        add("OGR");
        add("SRN");
        add("RCP");
        add("CUS");
        add("INC");
        add("GCC");
    }};

    /**
     * This code is under the assumption that no table is both openY and openZ. It also assumes that openZ can only have
     * one open code. It also uses list of possible openCodes for the openZ.
     * @param dimensionMembers
     * @param xbrlTableLights
     * @param openCodeMultiMap
     * @return
     */
    public static String checkForOpenCode(List<XbrlDimensionMember> dimensionMembers,
                                          List<XbrlTableLight> xbrlTableLights,
                                          Multimap<String, OpenCodeEntry> openCodeMultiMap) {
        //No open datapoint should arrive in more than one table
        if (xbrlTableLights.size() != 1) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        Collection<OpenCodeEntry> openCodeEntries = openCodeMultiMap.get(xbrlTableLights.get(0).getTableCode());
        if(xbrlTableLights.get(0).isOpenY()){
            for (XbrlDimensionMember member : dimensionMembers) {
                for(OpenCodeEntry openCodeEntry : openCodeEntries){
                    if(openCodeEntry.getDimensionCode().equals(member.getDimension().getDimensionCode())){
                        if(!builder.toString().equals("")){
                            builder.append(XBRL_OPEN_DELIMETER);
                        }
                        builder.append(member.getValue()).
                                append(XBRL_OPEN_DELIMITER_OPEN_BOUNDARY).
                                append(openCodeEntry.getTableOffset()).
                                append(XBRL_OPEN_DELIMITER_CLOSE_BOUNDARY);
                    }
                }
            }
            return builder.toString();
        } else if (xbrlTableLights.get(0).isOpenZ()){
            String openCode = "";
            for(XbrlDimensionMember member : dimensionMembers){
                if(openDimensions.contains(member.getDimension().getDimensionCode())){
                    openCode = member.getValue();
                }
            }
            return openCode;
        }
        return "";
    }


    public static String createDatapointKey(Collection<XbrlDimensionMember> members, XbrlMetric metric){
        List<String> keyParts = new ArrayList<>();
        for(XbrlDimensionMember member : members){
            String memberValue = member.getValue();
            if(openDimensions.contains(member.getDimension().getDimensionCode())){
                memberValue = "x999";
            }
            String keyPart ="eba_dim:"+ member.getDimension().getDimensionCode() +":"+ memberValue+"-";
            keyParts.add(keyPart);
        }
        Collections.sort(keyParts);
        StringBuilder builder = new StringBuilder();
        for(String keyPart : keyParts){
            builder.append(keyPart);
        }
        builder.append("eba_met:").append(metric.getMetricCode());
        return builder.toString();
    }
}
