/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lombardrisk.xbrl.checker.csv;

import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.io.dozer.CsvDozerBeanWriter;
import org.supercsv.io.dozer.ICsvDozerBeanReader;
import org.supercsv.io.dozer.ICsvDozerBeanWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Util class to read and write a csv using the SuperCsv library. The specialisation elements are given by the
 * CsvIntegration interface
 *
 * @author Cesar Tron-Lozai
 */
public final class CsvUtil {

    private CsvUtil() {
    }

    /**
     * Parse the csv data provided by the reader into a Collection of POJO T using the parameters provided in the
     * csvIntegration object
     *
     * @param <T> Class of the POJO to be created
     * @param reader reader object providing the CSV data
     * @param csvIntegration interface providing all the CSV parameters
     * @param hasHeader must set to true if the first line of the CSV represents a header
     * @return
     * @throws java.io.IOException
     */
    public static <T> List<T> parse(Reader reader, CsvDescriptor<T> csvIntegration, boolean hasHeader) throws IOException {
        try (ICsvDozerBeanReader beanReader = new CsvDozerBeanReader(reader, csvIntegration.getCsvPreference())) {
            if (hasHeader) {
                beanReader.getHeader(true);
            }
            beanReader.configureBeanMapping(csvIntegration.getBeanClass(), csvIntegration.getReadStringMapping());
            List<T> elements = new ArrayList<>();
            T element;
            while ((element = beanReader.read(csvIntegration.getBeanClass(), csvIntegration.getReadingCellProcessors())) != null) {
                elements.add(element);
            }
            return elements;
        }
    }

    public static <T> void write(Writer writer, CsvDescriptor<T> csvIntegration, Collection<T> pojos, boolean writeHeader) throws IOException {
        try (ICsvDozerBeanWriter beanWriter = new CsvDozerBeanWriter(writer, csvIntegration.getCsvPreference())) {
            // configure the mapping from the fields to the CSV columns
            beanWriter.configureBeanMapping(csvIntegration.getBeanClass(), csvIntegration.getWriteStringMapping());

            if (writeHeader) {
                beanWriter.writeHeader(csvIntegration.getWriteStringMapping());
            }

            //write the pojo
            for (final T pojo : pojos) {
                beanWriter.write(pojo, csvIntegration.getWritingCellProcessors());
            }
        }
    }
}
