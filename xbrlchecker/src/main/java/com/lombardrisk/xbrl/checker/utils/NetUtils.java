package com.lombardrisk.xbrl.checker.utils;

import com.lombardrisk.xbrl.checker.AppConstants;
import com.lombardrisk.xbrl.checker.config.Config;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

/**
 * Created by Cesar on 10/05/2014.
 */
public final class NetUtils {

    public static final String LOCALHOST = "localhost";

    private NetUtils() {
    }

    public static String getIpAddress() {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        return ipAddress;
    }


    /**
     * Adds the host to the uri builder using the Config class. If the host is localhost, the port will be added.
     * @param uriBuilder
     * @return
     */
    public static UriBuilder buildHost(UriBuilder uriBuilder) {
        final String host = Config.INSTANCE.getString("app.host");
        final int port = Config.INSTANCE.getInt("app.http.port");
        final String scheme = Config.INSTANCE.getString("app.scheme");
        uriBuilder.host(host).scheme(scheme);
        if (!AppConstants.HTTPS.equals(scheme)) {
            uriBuilder.port(port);
        }
        return uriBuilder;
    }
}
