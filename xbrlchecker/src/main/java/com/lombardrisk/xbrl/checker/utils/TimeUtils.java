package com.lombardrisk.xbrl.checker.utils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cesar on 11/05/2014.
 */
public final class TimeUtils {

    private TimeUtils() {
    }

    public static long getTimeDifferenceInSeconds(Calendar c1, Calendar c2, TimeUnit timeUnit){
        final long diffMillis = c1.getTimeInMillis() - c2.getTimeInMillis();
        return timeUnit.convert(diffMillis, TimeUnit.MILLISECONDS);
    }
}
