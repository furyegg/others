package com.lombardrisk.xbrl.checker.ejb.email;

import com.google.common.base.Predicate;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Cesar on 21/05/2014.
 */
public class HouseKeptSortedSetMultiMap<K, V> {
    private final TreeMultimap<K, V> multiMap;


    public HouseKeptSortedSetMultiMap(TreeMultimap<K, V> multiMap) {
        this.multiMap = multiMap;
    }

    public void put(K key, V value) {
        multiMap.put(key, value);
    }


    public SortedSet<V> houseKeepTail(K key, V compareTo) {
        doHouseKeepTail(key, compareTo);
        return multiMap.get(key);
    }

    private void doHouseKeepTail(K key, V compareTo) {
        SortedSet<V> toKeep = new TreeSet<>();
        for (V v : multiMap.get(key)) {
            if (multiMap.valueComparator().compare(compareTo, v) >= 0) {
                toKeep.add(v);
            } else {
                break;
            }
        }
        multiMap.replaceValues(key, toKeep);
    }
}
