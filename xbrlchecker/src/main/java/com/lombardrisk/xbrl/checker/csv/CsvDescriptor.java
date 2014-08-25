package com.lombardrisk.xbrl.checker.csv;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.prefs.CsvPreference;

/**
 * Interface to describing how to marshal and unmarshal a POJO and it's CSV
 * representation.
 *
 * @author Cesar Tron-Lozai
 */
public interface CsvDescriptor<T> {

    /**
     * Mapping of the column of the csv and the attribute of POJO T
     *
     * @return
     */
    String[] getReadStringMapping();
    
    String[] getWriteStringMapping();

    /**
     * Cells processors to be used when reading a CSV
     *
     * @return
     */
    CellProcessor[] getReadingCellProcessors();

    /**
     * Cells processors to be used when writing a CSV
     *
     * @return
     */
    CellProcessor[] getWritingCellProcessors();

    /**
     * Class of the POJO T
     *
     * @return
     */
    Class<T> getBeanClass();

    /**
     * Csv preference
     *
     * @return
     */
    CsvPreference getCsvPreference();
}
