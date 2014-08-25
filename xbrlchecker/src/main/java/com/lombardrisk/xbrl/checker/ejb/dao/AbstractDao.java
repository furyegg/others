package com.lombardrisk.xbrl.checker.ejb.dao;

import com.lombardrisk.xbrl.checker.model.entities.JpaEntity;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Cesar on 09/05/2014.
 */
public abstract class AbstractDao implements Serializable {

    protected <T extends  JpaEntity> T saveOrUpdate(T entity, EntityManager em) {
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        } else {
            T t = em.merge(entity);
            em.flush();
            return t;
        }
    }

    protected <T> T singleResultFromList(List<T> results) {
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }
}
