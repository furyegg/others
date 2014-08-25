package com.lombardrisk.xbrl.checker.ejb;

import com.lombardrisk.xbrl.checker.AppConstants;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.controller.XbrlCheckerController;
import com.lombardrisk.xbrl.checker.ejb.dao.UserDao;
import com.lombardrisk.xbrl.checker.ejb.email.EmailService;
import com.lombardrisk.xbrl.checker.model.entities.UserEmailValidationRequest;
import com.lombardrisk.xbrl.checker.utils.EmailTemplateUtil;
import com.lombardrisk.xbrl.checker.utils.NetUtils;
import com.lombardrisk.xbrl.checker.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cesar on 09/05/2014.
 */
@Stateless
public class EmailValidationService implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(EmailValidationService.class);
    public static final String EMAIL_HASH_PARAM = "emailHash";
    public static final String TOKEN_PARAM = "token";

    @Inject
    private UserDao userDao;

    @Inject
    private EmailService emailService;

    public void makeEmailValidationRequest(String email) {
        final int emailHash = email.hashCode();
        final String token = UUID.randomUUID().toString();
        final Calendar now = Calendar.getInstance();
        final UserEmailValidationRequest oldRequest = userDao.findUserEmailValidationRequest(emailHash);
        if (oldRequest != null) {
            final TimeUnit timeUnit = TimeUnit.valueOf(Config.INSTANCE.getString("validate.user.email.minimum.interval.between.email.unit"));
            final long timeoutAmount = Config.INSTANCE.getLong("validate.user.email.minimum.interval.between.email.amount");
            if(TimeUtils.getTimeDifferenceInSeconds(Calendar.getInstance(), oldRequest.getRequestTime(), timeUnit) < timeoutAmount){
                log.info(MessageFormat.format("Request for email {0} is not older than {1} {2}. I am not sending another email", oldRequest.getEmailAddress(), timeoutAmount, timeUnit));
                return;
            }

            log.info("Overriding existing request for email {}, hash:{}", email, emailHash);
            oldRequest.setToken(token);
            oldRequest.setRequestTime(now);
            userDao.saveUserEmailValidationRequest(oldRequest);
        } else {
            log.info("New request for email {}, hash:{}", email, emailHash);
            final UserEmailValidationRequest request = new UserEmailValidationRequest(email, emailHash, token, now);
            userDao.saveUserEmailValidationRequest(request);
        }
        log.info("Request for email {} with token {}", email, token);

        final String rootPath = Config.INSTANCE.getString("app.root.path");
        final String restUserCheckPath = Config.INSTANCE.getString("rest.user.check.path");

        final String fromAddress = Config.INSTANCE.getString("from.email");
        final String subject = Config.INSTANCE.getString("validate.user.email.subject");

        final UriBuilder uriBuilder = UriBuilder.fromPath(rootPath + "/" + restUserCheckPath).
                queryParam(EMAIL_HASH_PARAM, emailHash).
                queryParam(TOKEN_PARAM, token);
        NetUtils.buildHost(uriBuilder);
        final URI uri = uriBuilder.build();

        final String emailContent;
        try {
            final Map<String, String> params = new HashMap<>();
            params.put("[link]", uri.toString());
            params.put("[LEGAL_PAGE_URL]", XbrlCheckerController.getLegalPageUrl());
            emailContent = EmailTemplateUtil.loadEmail(AppConstants.EMAIL_NOT_REGISTERED_HTML, params);
            final String ip = NetUtils.getIpAddress();
            emailService.sendHtmlImageEmail(email, fromAddress, subject, emailContent, null, ip);
        } catch (IOException e) {
            log.error("Could not send email", e);
        }
    }
}
