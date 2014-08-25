package com.lombardrisk.xbrl.render.util;

import org.apache.commons.io.FilenameUtils;

/**
 * Created by Cesar on 16/06/2014.
 */
public final class FileUtils {
    private FileUtils() {
    }


    /**
     * Returns the base file name (without extension). It handles multiple extensions. For example "foo.xml.gz" will
     * be returned as "foo".
     * @param fileName
     * @return
     */
    public static String getBaseFileName(String fileName) {
        while (fileName.contains(".")) {
            fileName = FilenameUtils.getBaseName(fileName);
        }
        return fileName;
    }
}
