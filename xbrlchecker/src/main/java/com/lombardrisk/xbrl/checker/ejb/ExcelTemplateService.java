package com.lombardrisk.xbrl.checker.ejb;

import com.lombardrisk.xbrl.checker.config.Config;

import javax.ejb.Singleton;
import java.io.File;
import java.io.Serializable;

/**
 * Created by Cesar on 08/05/2014.
 */
@Singleton
public class ExcelTemplateService implements Serializable {
    public static final String TEMPLATE_EXTENSION = ".xlsx";
    private static String TEMPLATE_FOLDER_PATH = "";

    public File getExcelTemplate(String returnCode) {
        String templatePath = Config.INSTANCE.getString("eba.templates.path");
        if (!templatePath.endsWith(File.separator)) {
            templatePath += File.separator;
        }
        final File template = new File(templatePath + returnCode + TEMPLATE_EXTENSION);
        if (template.exists()) {
            return template;
        } else {
            return null;
        }
    }
}
