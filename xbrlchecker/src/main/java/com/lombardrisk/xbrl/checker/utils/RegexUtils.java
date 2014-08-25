package com.lombardrisk.xbrl.checker.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cesar on 11/05/2014.
 */
public final class RegexUtils {

    private static final Pattern TABLE_CODE_PATTERN = Pattern.compile("[\\w] [\\d]{2}\\.[\\d]{2}(\\.[\\w])?");

    public static final List<String> getTableCodes(String s) {
        final List<String> tableCodes = new ArrayList<>();
        final Matcher m = TABLE_CODE_PATTERN.matcher(s);
        while(m.find()) {
            tableCodes.add(m.group());
        }
        return tableCodes;
    }
}
