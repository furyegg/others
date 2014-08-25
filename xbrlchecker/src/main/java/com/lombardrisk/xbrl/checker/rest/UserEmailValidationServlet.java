package com.lombardrisk.xbrl.checker.rest;

import ch.qos.logback.core.util.TimeUtil;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.ejb.dao.UserDao;
import com.lombardrisk.xbrl.checker.model.entities.User;
import com.lombardrisk.xbrl.checker.model.entities.UserEmailValidationRequest;
import com.lombardrisk.xbrl.checker.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cesar on 09/05/2014.
 */
@Path("/check")
public class UserEmailValidationServlet {

    private static final Logger log = LoggerFactory.getLogger(UserEmailValidationServlet.class);
    private static final String USER_PAGE = "../resources/index.xhtml";

    @Inject
    private UserDao userDao;

    @GET
    @Path("/validate")
    public Response printMessage(@QueryParam("emailHash") String emailHashStr, @QueryParam("token") String token) {
        try {
            final int emailHash = Integer.parseInt(emailHashStr);
            log.info("Validating email {} with token {}", emailHash, token);

            final TimeUnit timeUnit = TimeUnit.valueOf(Config.INSTANCE.getString("validate.user.timeout.unit"));
            final long timeoutAmount = Config.INSTANCE.getLong("validate.user.timeout.amount");
            UserEmailValidationRequest request = userDao.findUserEmailValidationRequest(emailHash);
            if (request == null) {
                log.info("No request found for email hash {}", emailHash);
                return Response.status(400).entity("WRONG REQUEST").build();
            } else if (!request.getToken().equals(token)) {
                log.info("Request found for email {} but token incorrect", request.getEmailAddress());
                return Response.status(400).entity("TOKEN INCORRECT").build();
            } else if (TimeUtils.getTimeDifferenceInSeconds(Calendar.getInstance(), request.getRequestTime(), timeUnit) > timeoutAmount) {
                return Response.status(400).entity("REQUEST TIMEOUT. PLEASE GENERATE ANOTHER REQUEST").build();
            } else {
                final User olderUser = userDao.getUser(request.getEmailAddress());
                User user;
                if (olderUser == null) {
                    user = new User(request.getEmailAddress());
                    userDao.createUser(user);
                } else {
                    log.info("User {} already exist", olderUser.getEmailAddress());
                    user = olderUser;
                }
                userDao.deleteUserEmailValidationRequest(request);

                URI uri = UriBuilder.fromPath(USER_PAGE).queryParam("email", user.getEmailAddress()).build();
                log.info("Validated email {}. A new user has been created", user.getEmailAddress());
                return Response.temporaryRedirect(uri).build();
            }
        }catch(Exception e){
            log.warn("Error validating email address: ", e.getMessage());
            return Response.status(400).entity("ERROR").build();
        }
    }
}
