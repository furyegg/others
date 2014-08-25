package com.lombardrisk.xbrl.checker.ejb.dao;

import com.lombardrisk.xbrl.checker.model.entities.User;
import com.lombardrisk.xbrl.checker.model.entities.UserEmailValidationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Cesar on 09/05/2014.
 */
@Stateless
public class UserDao extends AbstractDao {
    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    @PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName = "xbrlCheckerDS")
    private EntityManager em;

    public void saveUserEmailValidationRequest(UserEmailValidationRequest request) {
        saveOrUpdate(request, em);
    }

    public UserEmailValidationRequest findUserEmailValidationRequest(int emailHash) {
        TypedQuery<UserEmailValidationRequest> query = em.createNamedQuery("UserEmailValidationRequest.findByEmailHash",
                UserEmailValidationRequest.class);
        query.setParameter("emailHash", emailHash);
        final List<UserEmailValidationRequest> result = query.getResultList();
        return singleResultFromList(result);
    }

    public void createUser(User user) {
        saveOrUpdate(user, em);
    }

    public void deleteUserEmailValidationRequest(UserEmailValidationRequest request) {
        UserEmailValidationRequest entity = em.find(UserEmailValidationRequest.class, request.getId());
        if(entity != null){
            em.remove(entity);
            log.info("Deleting userEmailValidationRequest");
        }else{
            log.info("Could not find UserEmailValidationRequest for email {}. Nothing to delete");
        }
    }

    public User getUser(String emailAddress) {
        TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
        query.setParameter("emailAddress", emailAddress);
        return singleResultFromList(query.getResultList());
    }

    public List<User> getAllUsers(){
        final TypedQuery<User> query = em.createQuery("select user from com.lombardrisk.xbrl.checker.model.entities.User user", User.class);
        return query.getResultList();
    }
}
