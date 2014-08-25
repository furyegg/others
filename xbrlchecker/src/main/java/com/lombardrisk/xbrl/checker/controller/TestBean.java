package com.lombardrisk.xbrl.checker.controller;

import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.ejb.EmailValidationService;
import com.lombardrisk.xbrl.checker.ejb.dao.ValidationLookupRequestDao;
import com.lombardrisk.xbrl.checker.ejb.email.EmailService;
import com.lombardrisk.xbrl.checker.model.ValidationLookupRequestCountDto;
import com.lombardrisk.xbrl.checker.model.entities.ValidationLookupRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * Created by Cesar on 08/05/2014.
 */
@ViewAccessScoped
@Named
public class TestBean implements Serializable{
    private static final Logger log = LoggerFactory.getLogger(TestBean.class);
    @Inject
    private ValidationLookupRequestDao dao;

    @Inject
    private EmailValidationService emailValidationService;

    @Inject
    private EmailService emailService;

    private String emailBody;

    private String emailAddress;

    public void test(){
        ValidationLookupRequest r = new ValidationLookupRequest();
        dao.addValidationLookupRequest(r);
        log.info("#Request: "+dao.getAll().size());
        log.info("test");
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void submit(){
        log.info("submit222");
        emailValidationService.makeEmailValidationRequest(emailAddress);
    }

    public void sendEmail(){
//        log.info("Sending email");
//        emailService.sendHtmlEmail("cesar.tron-lozai@lombardrisk.com", "xbrl@lombardrisk.com", "Test", emailBody, null);
    }

    public String getEmailBody() {
        return emailBody;
    }

    public static Logger getLog() {
        return log;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public void leaderBoard(){
        List<ValidationLookupRequestCountDto> leaderBoardsEntries = dao.getLeaderBoardsEntries(4);
        for (ValidationLookupRequestCountDto e : leaderBoardsEntries) {
            log.info("{}: {}", e.getValidationId(), e.getCount());
        }

    }
}
