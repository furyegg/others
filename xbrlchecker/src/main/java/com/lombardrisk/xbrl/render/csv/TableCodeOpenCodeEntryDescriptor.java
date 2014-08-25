package com.lombardrisk.xbrl.render.csv;

import com.lombardrisk.xbrl.checker.csv.CsvDescriptor;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.prefs.CsvPreference;

/**
 * Created by Cesar on 12/06/2014.
 */
public class TableCodeOpenCodeEntryDescriptor implements CsvDescriptor<TableCodeOpenCodeEntry>{
    @Override
    public String[] getReadStringMapping() {
        return new String[]{"tableCode", "openCodeEntry.dimensionCode", "openCodeEntry.tableOffset"};
    }

    @Override
    public String[] getWriteStringMapping() {
        return new String[]{"tableCode", "openCodeEntry.dimensionCode", "openCodeEntry.tableOffset"};
    }

    @Override
    public CellProcessor[] getReadingCellProcessors() {
        return new CellProcessor[]{new NotNull(), new NotNull(), new NotNull()};
    }

    @Override
    public CellProcessor[] getWritingCellProcessors() {
        return new CellProcessor[]{new NotNull(), new NotNull(), new NotNull()};
    }

    @Override
    public Class<TableCodeOpenCodeEntry> getBeanClass() {
        return TableCodeOpenCodeEntry.class;
    }

    @Override
    public CsvPreference getCsvPreference() {
        return CsvPreference.STANDARD_PREFERENCE;
    }
}
