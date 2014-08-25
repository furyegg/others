package com.lombardrisk.xbrl.checker.ejb;

import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by Cesar on 27/05/2014.
 */
@RequestScoped
@Named
public class LoginBean {

    private static final Logger log = LoggerFactory.getLogger(LoginBean.class);

    private String username;
    private String password;

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            checkIpAddressRestriction();
            request.login(this.username, this.password);
            log.info("{} has logged in with IP {}", this.username, NetUtils.getIpAddress());
        } catch (ServletException e) {
            log.warn("Login failed: {}", e.getMessage());
//            context.addMessage(null, new FacesMessage("Login failed."));
            return "/resources/error.xhtml";
        }
        return "/resources/admin.xhtml";
    }

    private void checkIpAddressRestriction() throws ServletException {
        final List<String> ipList = Config.INSTANCE.getList("admin.page.ip.lists");
        final String ip = NetUtils.getIpAddress();
        if (!ipList.contains(ip)) {
            throw new ServletException(MessageFormat.format("Not authorised ip \"{0}\"", ip));
        }
    }

    public String logout() {
        try {
            return "admin.xhtml";
        } finally {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            try {
                Principal principal = context.getExternalContext().getUserPrincipal();
                // closing the session
                HttpSession session = request.getSession(false);
                request.logout();

                if (session != null) {
                    session.invalidate();
                }
                if (principal != null) {
                    log.info(principal.getName() + " has logged out");
                }
            } catch (ServletException e) {
                log.error("Could not logout", e);
//            context.addMessage(null, new FacesMessage("Logout failed."));
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
