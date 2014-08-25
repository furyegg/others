package com.lombardrisk.xbrl.checker.utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.*;
import java.net.URL;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Hoang Nguyen on 09/05/2014.
 */
public final class EmailTemplateUtil {

    private static final Logger log = LoggerFactory.getLogger(EmailTemplateUtil.class);


    public static String loadEmail(String templateName, Map<String, String> param) throws IOException {
        final InputStream in = EmailTemplateUtil.class.getClassLoader().getResourceAsStream(templateName);
        if(in ==null){
            throw new IOException("Could not find template " + templateName);
        }

        String emailContent = IOUtils.toString(in);

        if(param != null){
            for (Map.Entry<String, String> entry : param.entrySet()) {
                emailContent =  StringUtils.replace(emailContent, entry.getKey(), entry.getValue());
            }
        }
        return emailContent;
    }

}
