package com.lombardrisk.xbrl.render.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Cesar on 17/06/2014.
 */
public final class UUIDUtils {

    public static final String SEPARATOR = "-";
    public static final int SEPARATOR_PERIOD = 4;

    private UUIDUtils() {
    }


    /**
     * Return the 64 most significant bits of a random UUID. The return string is the hexadecimal representation of a long.
     * <p>
     *  Example
     * <code>
     *  684adbe7d892447c
     * </code>
     * @return
     */
    public static String smallHexUUID() {
        final long n = UUID.randomUUID().getMostSignificantBits();
        return Long.toHexString(n);
    }

    /**
     * Return the 64 most significant bits of a random UUID. The return string is the hexadecimal representation of a long,
     * separated with a character to increase readability.
     * <p>
     *  Example
     * <code>
     *  21ea-087d-b189-447d
     * </code>
     * @return
     */
    public static String smallHexUUIDHumanReadable(){
        final String smallUuid = smallHexUUID();
        return humanReadableUUID(smallUuid);
    }


    public static String humanReadableUUID(String uuid) {
        if (uuid == null) {
            return null;
        }
        final List<String> elements = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < uuid.length(); i++) {
            if (i > 0 && i % SEPARATOR_PERIOD == 0) {
                elements.add(builder.toString());
                builder = new StringBuilder();
            }
            builder.append(uuid.charAt(i));
        }
        elements.add(builder.toString());
        return Joiner.on(SEPARATOR).join(elements);
    }

    public static String parseHumanReadableUUID(String humanReadableUUID) {
        final Splitter splitter = Splitter.on(SEPARATOR);
        final StringBuilder builder = new StringBuilder();
        for (String s : splitter.split(humanReadableUUID)) {
            builder.append(s);
        }
        return builder.toString();
    }
}
