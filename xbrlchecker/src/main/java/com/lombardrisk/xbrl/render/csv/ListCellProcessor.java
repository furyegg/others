package com.lombardrisk.xbrl.render.csv;

import com.google.common.base.Splitter;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cesar on 11/06/2014.
 */
public class ListCellProcessor extends CellProcessorAdaptor {

    private final String separatorString;
    private final String openBoundary;
    private final String closeBoundary;
    private final Pattern listPattern;
    private final Splitter splitter;


    public ListCellProcessor(String separatorString, String openBoundary, String closeBoundary) {
        this.separatorString = separatorString;
        this.openBoundary = openBoundary;
        this.closeBoundary = closeBoundary;
        listPattern = Pattern.compile(MessageFormat.format("\\{0}(.*)\\{1}", openBoundary, closeBoundary));
        splitter = Splitter.on(separatorString).trimResults();
    }

    public ListCellProcessor(CellProcessor next, String separatorString, String openBoundary, String closeBoundary) {
        super(next);
        this.separatorString = separatorString;
        this.openBoundary = openBoundary;
        this.closeBoundary = closeBoundary;
        listPattern = Pattern.compile(MessageFormat.format("\\{0}(.*)\\{1}", openBoundary, closeBoundary));
        splitter = Splitter.on(separatorString).trimResults();
    }

    @Override
    public Object execute(Object value, CsvContext csvContext) {
        validateInputNotNull(value, csvContext);
        final String listValue = value.toString();
        Matcher m = listPattern.matcher(listValue);
        if (m.matches()) {
            String elementsString = m.group(1);
            return splitter.splitToList(elementsString);
        }
        throw new SuperCsvCellProcessorException(
                MessageFormat.format("Could not parse list ''{0}''", listValue), csvContext, this);

    }
}
