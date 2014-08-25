package com.lombardrisk.xbrl.render.util;

import com.healthmarketscience.jackcess.Row;

import java.util.Comparator;

/**
 * Created by Bartosz Jedrzejewski on 13/06/14.
 */
public class AxisOrdinateComparator implements Comparator<Row> {
    @Override
    public int compare(Row o1, Row o2) {
        return ((Integer)o1.get("Order")).compareTo((Integer)o2.get("Order"));
    }
}
