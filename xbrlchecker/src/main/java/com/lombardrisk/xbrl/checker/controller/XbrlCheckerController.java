package com.lombardrisk.xbrl.checker.controller;

import com.lombardrisk.xbrl.checker.AppConstants;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.ejb.EmailValidationService;
import com.lombardrisk.xbrl.checker.ejb.ExcelTemplateService;
import com.lombardrisk.xbrl.checker.ejb.ValidationInfoService;
import com.lombardrisk.xbrl.checker.ejb.dao.UserDao;
import com.lombardrisk.xbrl.checker.ejb.dao.ValidationLookupRequestDao;
import com.lombardrisk.xbrl.checker.ejb.email.EmailService;
import com.lombardrisk.xbrl.checker.model.ReturnInfo;
import com.lombardrisk.xbrl.checker.model.ValidationInfo;
import com.lombardrisk.xbrl.checker.model.ValidationLookupRequestCountDto;
import com.lombardrisk.xbrl.checker.model.entities.User;
import com.lombardrisk.xbrl.checker.model.entities.ValidationAttachmentRequest;
import com.lombardrisk.xbrl.checker.model.entities.ValidationLookupRequest;
import com.lombardrisk.xbrl.checker.utils.EmailTemplateUtil;
import com.lombardrisk.xbrl.checker.utils.NetUtils;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cesar on 09/05/2014.
 */
@ViewAccessScoped
@Named
public class XbrlCheckerController implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(XbrlCheckerController.class);
    @Inject
    private UserDao userDao;

    @Inject
    private ValidationInfoService validationInfoService;

    @Inject
    private ExcelTemplateService excelTemplateService;

    @Inject
    private ValidationLookupRequestDao validationLookupRequestDao;

    @Inject
    private EmailValidationService emailValidationService;

    @Inject
    private EmailService emailService;

    private boolean emailWasSent = false;

    private String emailAddress;

    private boolean emailAddressValid;
    private boolean emailAddressMatchUser;

    private boolean firstEmailCheck;

    private String errorCode;
    private ValidationInfo validationInfo;

    private File excelTemplate;

    private User user;

    private List<ValidationLookupRequestCountDto> leaderBoardEntries;

    private List<ReturnInfo> returnInfoList;

    public XbrlCheckerController() {
    }

    public boolean isEmailWasSent() {
        return emailWasSent;
    }

    public void setEmailWasSent(boolean emailWasSent) {
        this.emailWasSent = emailWasSent;
    }

    public List<ReturnInfo> getReturnInfoList() {
        if (validationInfo == null) {
            return new ArrayList<>();
        }
        returnInfoList = validationInfoService.getReturnInfoList(validationInfo);
        return returnInfoList;
    }

    @PostConstruct
    public void init() {
        firstEmailCheck = true;
        emailAddressValid = false;
        emailAddressMatchUser = false;
        leaderBoardEntries = validationLookupRequestDao.getLeaderBoardsEntries(getNumberOfLeaderBoardEntries());
    }

    public void checkEmailAddressInit() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            checkEmailAddress(null);
        }
    }

    public void checkEmailAddress(AjaxBehaviorEvent event) {
        firstEmailCheck = false;
        if (emailAddress != null) {
            if (EmailValidator.isEmailValid(emailAddress)) {
                emailAddressValid = true;
                user = userDao.getUser(emailAddress.toLowerCase());
                emailAddressMatchUser = user != null;
                checkErrorCode(null);
            } else {
                emailAddressValid = false;
                emailAddressMatchUser = false;
            }
        }
    }

    public void checkErrorCode(AjaxBehaviorEvent event) {
        String validationId = ValidationIdValidator.getValidationId(errorCode);
        if (user != null && validationId != null) {
            validationInfo = validationInfoService.getValidationInfo(validationId.toLowerCase());
        } else {
            validationInfo = null;
        }
    }

    public void emailValidationRequest() {
        emailValidationService.makeEmailValidationRequest(emailAddress);
    }

    public void submit() {
        emailWasSent = false;
        if (validationInfo != null && user!= null) {
            final ValidationLookupRequest lookupRequest = new ValidationLookupRequest();
            lookupRequest.setTimeStamp(Calendar.getInstance());
            lookupRequest.setUser(user);
            lookupRequest.setValidationId(validationInfo.getCode());
            lookupRequest.setIpAddress(NetUtils.getIpAddress());

            validationLookupRequestDao.addValidationLookupRequest(lookupRequest);

            leaderBoardEntries = validationLookupRequestDao.getLeaderBoardsEntries(getNumberOfLeaderBoardEntries());
            excelTemplate = excelTemplateService.getExcelTemplate(validationInfo.getCode());
        }
    }

    public void sendEmailTemplate(AjaxBehaviorEvent event) {
        if (user != null && excelTemplate != null && validationInfo != null) {
            final TimeUnit maxTimeUnit = TimeUnit.valueOf(Config.INSTANCE.getString("request.attachment.max.time.unit"));
            final long maxTimeAmount = Config.INSTANCE.getLong("request.attachment.max.time.amount");
            final int maxAmount = Config.INSTANCE.getInt("request.attachment.max.amount");

            final int attempts = validationLookupRequestDao.getNumberOfRequest(user, maxTimeUnit, maxTimeAmount);

            if (attempts <= maxAmount) {
                final ValidationAttachmentRequest request = new ValidationAttachmentRequest();
                request.setIpAddress(NetUtils.getIpAddress());
                request.setValidationId(validationInfo.getCode());
                request.setUser(user);
                request.setTimeStamp(Calendar.getInstance());
                validationLookupRequestDao.addValidationAttachmentRequest(request);
                try {
                    final Map<String, String> params = new HashMap<>();
                    params.put("[errorCode]", validationInfo.getCode());
                    params.put("[validationScope]", validationInfo.getRawScope());
                    params.put("[formula]", validationInfo.getFormula());
                    params.put("[LEGAL_PAGE_URL]", getLegalPageUrl());

                    final StringBuilder builder = new StringBuilder();
                    builder.append("Returns in scope<br />");
                    for (ReturnInfo returnInfo : getReturnInfoList()) {
                        builder.append(returnInfo.getCode()).append(" - ").append(returnInfo.getLabel()).append("<br />");
                    }
                    params.put("[returns]", builder.toString());

                    final String emailContent = EmailTemplateUtil.loadEmail("email-template.html", params);
                    log.info("Sending email with attachment to " + user.getEmailAddress());
                    final String ip = NetUtils.getIpAddress();
                    emailService.sendHtmlImageEmail(user.getEmailAddress(), Config.INSTANCE.getString("from.email"),
                            "Your attachment for validation " + validationInfo.getCode(), emailContent, excelTemplate, ip);
                } catch (IOException e) {
                    log.error("Could not send email", e);
                }
            }else{
                log.info(MessageFormat.format("Maximum number of attachment request reached for user {0} (MAX {1} request per {2} {3}",
                        user.getEmailAddress(), String.valueOf(maxAmount), String.valueOf(maxTimeAmount), maxTimeUnit.toString()));
            }
        }
        emailWasSent = true;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isEmailAddressValid() {
//        if (firstEmailCheck) {
//            return true;
//        }
        return emailAddressValid;
    }

    public boolean isEmailAddressMatchUser() {
        return emailAddressMatchUser;
    }

    public boolean isShowEmailTick() {
        return emailAddressValid;
    }

    public void setEmailAddressValid(boolean emailAddressValid) {
        this.emailAddressValid = emailAddressValid;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public boolean isErrorCodeValid() {
        return validationInfo != null;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ValidationInfo getValidationInfo() {
        return validationInfo;
    }

    public File getExcelTemplate() {
        return excelTemplate;
    }

    public List<ValidationLookupRequestCountDto> getLeaderBoardEntries() {
        return leaderBoardEntries;
    }

    private int getNumberOfLeaderBoardEntries() {
        return Config.INSTANCE.getInt("ui.leader.board.entries");
    }

    public static String getLegalPageUrl() {
        final String rootPath = Config.INSTANCE.getString("app.root.path");
        final String legalPage = "/resources/legal.html";
        final UriBuilder builder = UriBuilder.fromPath(rootPath + legalPage);
        NetUtils.buildHost(builder);
        return builder.build().toString();
    }
}
