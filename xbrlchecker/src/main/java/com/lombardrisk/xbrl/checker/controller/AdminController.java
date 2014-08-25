package com.lombardrisk.xbrl.checker.controller;

import com.lombardrisk.xbrl.checker.ejb.dao.UserDao;
import com.lombardrisk.xbrl.checker.model.entities.User;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Cesar on 20/05/2014.
 */
@ViewAccessScoped
@Named
public class AdminController implements Serializable {

    @Inject
    private UserDao userDao;

    private List<User> users;

    private Calendar lastRefreshed;

    @PostConstruct
    public void init() {
        reloadUsers();
    }

    public void reloadUsers() {
        users = userDao.getAllUsers();
        lastRefreshed = Calendar.getInstance();
    }

    public List<User> getUsers() {
        return users;
    }

    public Calendar getLastRefreshed() {
        return lastRefreshed;
    }
}
