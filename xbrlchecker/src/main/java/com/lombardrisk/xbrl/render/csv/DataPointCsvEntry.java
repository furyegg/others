package com.lombardrisk.xbrl.render.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cesar on 10/06/2014.
 */
public class DataPointCsvEntry {

    private String key;
    private List<String> elements = new ArrayList<>();

    public DataPointCsvEntry(String key, List<String> elements) {
        this.key = key;
        this.elements = elements;
    }

    public DataPointCsvEntry() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }
}
