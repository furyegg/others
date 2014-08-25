package com.lombardrisk.xbrl.checker.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Cesar on 03/03/14.
 */

@ApplicationPath("/rest/")
public class MessageApplication extends Application {
    @SuppressWarnings("unchecked")
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(UserEmailValidationServlet.class));
    }
}

