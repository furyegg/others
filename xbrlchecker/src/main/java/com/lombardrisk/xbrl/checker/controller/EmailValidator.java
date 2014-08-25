package com.lombardrisk.xbrl.checker.controller;

/**
 * Created by Hoang Nguyen on 09/05/2014.
 */

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.validate.ClientValidator;
/**
 * Util class for validating email address
 */
public final class EmailValidator{
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-']+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)+$");

    public static boolean isEmailValid(String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }

}