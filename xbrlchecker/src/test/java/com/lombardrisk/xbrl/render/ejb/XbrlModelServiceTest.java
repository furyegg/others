package com.lombardrisk.xbrl.render.ejb;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.csv.CsvUtil;
import com.lombardrisk.xbrl.model.XbrlTable;
import com.lombardrisk.xbrl.render.csv.DataPointCsvEntry;
import com.lombardrisk.xbrl.render.csv.DataPointCsvEntryDescriptor;
import com.lombardrisk.xbrl.render.csv.OpenCodeEntry;
import com.lombardrisk.xbrl.render.util.OpenCodeUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Bartosz Jedrzejewski on 06/06/14.
 */
public class XbrlModelServiceTest {

    @Ignore
    @Test
    public void testBuildXbrlTable() throws Exception {
        XbrlModelService service = new XbrlModelService();
        service.init();
        XbrlTable xbrlTable = service.buildXbrlTable("C_30.00");
    }

    @Test@Ignore
    public void testCreateDataPointMap() throws IOException {
        XbrlModelService service = new XbrlModelService();
        service.init();
        Map<String, List<String>> dataPointsMap = service.createDataPointMap();
        List<DataPointCsvEntry> datapointsForCsv = new ArrayList<>();
        for(String key : dataPointsMap.keySet()){
            datapointsForCsv.add(new DataPointCsvEntry(key, dataPointsMap.get(key)));
        }
        FileWriter writer = new FileWriter("dpMappings.csv");
        CsvUtil.write(writer, new DataPointCsvEntryDescriptor(), datapointsForCsv, true);
    }
}
