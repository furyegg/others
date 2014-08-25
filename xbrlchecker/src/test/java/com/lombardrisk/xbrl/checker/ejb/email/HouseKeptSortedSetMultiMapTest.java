package com.lombardrisk.xbrl.checker.ejb.email;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import org.junit.Test;

import java.util.SortedSet;

import static org.junit.Assert.*;

public class HouseKeptSortedSetMultiMapTest {

    @Test
    public void testHouseKeep() throws Exception {
        TreeMultimap<String, Integer> map = TreeMultimap.create(Ordering.natural(), Ordering.natural().reverse());
        HouseKeptSortedSetMultiMap<String, Integer> houseKept = new HouseKeptSortedSetMultiMap<>(map);
        houseKept.put("a", 1);
        houseKept.put("a", 2);
        houseKept.put("a", 3);
        houseKept.put("a", 4);

        System.out.println(Joiner.on(",").join(map.get("a")));

        SortedSet<Integer> test = houseKept.houseKeepTail("a", 3);

        System.out.println(Joiner.on(",").join(test));
    }
}