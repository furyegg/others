package com.lombardrisk.xbrl.checker.ejb.email;


import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.util.concurrent.AtomicLongMap;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.utils.NetUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceFileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.tree.Tree;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.mail.Session;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Time;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

@Stateless
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public static final String EMAIL_SESSION_JNDI = "java:/Mail";

    @Resource(name = EMAIL_SESSION_JNDI)
    private Session mailSession;

    @Inject
    private EmailIpQuotaManager emailIpQuotaManager;


    @Asynchronous
    public void sendHtmlImageEmail(String toAddress, String fromAddress, String subject, String body, File attachment, String callerIp) {
        try {
            if (emailIpQuotaManager.checkForIpQuotas(callerIp)) {
                sendEmail(toAddress, fromAddress, subject, body, attachment);
            }
        } catch (EmailException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendEmail(String toAddress, String fromAddress, String subject, String body, File attachment) throws IOException, EmailException {

        ImageHtmlEmail email = new ImageHtmlEmail();

        final String resourcePath = Config.INSTANCE.getString("email.templates.location");
        final File resourceFolder = new File(resourcePath);
        if (resourceFolder == null) {
            throw new IOException("Could not find folder" + resourcePath);
        }
        email.setDataSourceResolver(new DataSourceFileResolver(resourceFolder));

        email.addTo(toAddress);
        email.setFrom(fromAddress);
        email.setSubject(subject);

        email.setHtmlMsg(body);

        email.setMailSession(mailSession);

        if (attachment != null) {
            EmailAttachment emailAttachment = new EmailAttachment();
            emailAttachment.setURL(attachment.toURI().toURL());

            email.attach(attachment);
        }
        email.send();
        log.info("Email sent");
    }
}
