package com.lombardrisk.xbrl.checker.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cesar on 09/05/2014.
 */
public class ValidationIdValidator {
    private static final Pattern VALIDATION_ID_PATTERN = Pattern.compile("(eba_)?(v[\\d]{4}_[\\w])");

    public static String getValidationId(String errorCode) {
        if(errorCode == null){
            return null;
        }
        Matcher m = VALIDATION_ID_PATTERN.matcher(errorCode);
        if (m.matches()) {
            return m.group(2);
        }
        return null;
    }
}
