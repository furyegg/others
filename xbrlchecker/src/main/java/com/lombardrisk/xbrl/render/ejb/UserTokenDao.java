package com.lombardrisk.xbrl.render.ejb;

import com.lombardrisk.xbrl.checker.ejb.dao.AbstractDao;
import com.lombardrisk.xbrl.checker.model.entities.User;
import com.lombardrisk.xbrl.checker.utils.NetUtils;
import com.lombardrisk.xbrl.render.model.entities.UserToken;
import com.lombardrisk.xbrl.render.model.entities.UserTokenUsage;
import com.lombardrisk.xbrl.render.model.exception.UserTokenException;
import com.lombardrisk.xbrl.render.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Cesar on 06/06/2014.
 */
@Stateless
public class UserTokenDao extends AbstractDao {
    private static final Logger log = LoggerFactory.getLogger(UserTokenDao.class);

    @PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName = "xbrlCheckerDS")
    private EntityManager em;


    /**
     * Creates a new UserToken with the given number of request and saves it to the DB
     * @param numberOfRequest
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserToken createUserToken(long numberOfRequest) throws UserTokenException {
        UserToken userToken = UserToken.createUserToken(numberOfRequest);
        log.info(MessageFormat.format("Creating user token ''{0}'' with {1} requests", userToken.getToken(), userToken.getOriginalRequests()));
        return saveOrUpdate(userToken, em);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserToken getToken(String token) throws UserTokenException {
        token = UUIDUtils.parseHumanReadableUUID(token);
        TypedQuery<UserToken> query = em.createQuery("select userToken from " +
                "com.lombardrisk.xbrl.render.model.entities.UserToken userToken where userToken.token = :token", UserToken.class);
        query.setParameter("token", token);
        UserToken userToken = singleResultFromList(query.getResultList());
        if (userToken == null) {
            throw new UserTokenException(MessageFormat.format("Token ''{0}'' is not a valid token", token));
        }
        return userToken;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserToken consumeUserToken(String token) throws UserTokenException {
        final UserToken userToken = getToken(token);
        if(userToken.getRemainingRequests() == 0){
            throw new UserTokenException(MessageFormat.format("Token ''{0}'' has no remaining requests", token));
        }
        userToken.setRemainingRequests(userToken.getRemainingRequests() -1 );
        final UserTokenUsage userTokenUsage = new UserTokenUsage();
        userTokenUsage.setIpAddress(NetUtils.getIpAddress());
        userTokenUsage.setTimeUsed(Calendar.getInstance());
        userTokenUsage.setUserToken(userToken);
        saveOrUpdate(userTokenUsage, em);
        log.info(MessageFormat.format("Consuming user token ''{0}''. Requests left: {1}/{2}",
                userToken.getToken(), userToken.getRemainingRequests(), userToken.getOriginalRequests()));
        return saveOrUpdate(userToken, em);
    }
}
