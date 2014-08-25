package com.lombardrisk.xbrl.checker.csv;

import com.lombardrisk.xbrl.checker.model.DeactivatedValidation;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.prefs.CsvPreference;

/**
 * Created by Cesar on 21/05/2014.
 */
public class DeactivatedValidationCsvDescriptor implements CsvDescriptor<DeactivatedValidation>{
    @Override
    public String[] getReadStringMapping() {
        return new String[]{"validationId"};
    }

    @Override
    public String[] getWriteStringMapping() {
        return new String[0];
    }

    @Override
    public CellProcessor[] getReadingCellProcessors() {
        return new CellProcessor[]{new NotNull()};
    }

    @Override
    public CellProcessor[] getWritingCellProcessors() {
        return new CellProcessor[0];
    }

    @Override
    public Class<DeactivatedValidation> getBeanClass() {
        return DeactivatedValidation.class;
    }

    @Override
    public CsvPreference getCsvPreference() {
        return CsvPreference.STANDARD_PREFERENCE;
    }
}
