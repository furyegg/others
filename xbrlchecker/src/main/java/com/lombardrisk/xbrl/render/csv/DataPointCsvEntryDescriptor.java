package com.lombardrisk.xbrl.render.csv;

import com.lombardrisk.xbrl.checker.csv.CsvDescriptor;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

/**
 * Created by Cesar on 10/06/2014.
 */
public class DataPointCsvEntryDescriptor implements CsvDescriptor<DataPointCsvEntry>{
    @Override
    public String[] getReadStringMapping() {
        return new String[]{"key", "elements"};
    }

    @Override
    public String[] getWriteStringMapping() {
        return new String[]{"key", "elements"};
    }

    @Override
    public CellProcessor[] getReadingCellProcessors() {
        return new CellProcessor[]{new NotNull(), new ListCellProcessor(",", "[", "]")};
    }

    @Override
    public CellProcessor[] getWritingCellProcessors() {
        return new CellProcessor[]{new NotNull(), new NotNull()};
    }

    @Override
    public Class<DataPointCsvEntry> getBeanClass() {
        return DataPointCsvEntry.class;
    }

    @Override
    public CsvPreference getCsvPreference() {
        return CsvPreference.STANDARD_PREFERENCE;
    }
}
