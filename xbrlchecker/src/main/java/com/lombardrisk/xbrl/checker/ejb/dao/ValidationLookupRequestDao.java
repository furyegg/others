package com.lombardrisk.xbrl.checker.ejb.dao;

import com.lombardrisk.xbrl.checker.model.ValidationLookupRequestCountDto;
import com.lombardrisk.xbrl.checker.model.entities.User;
import com.lombardrisk.xbrl.checker.model.entities.ValidationAttachmentRequest;
import com.lombardrisk.xbrl.checker.model.entities.ValidationLookupRequest;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cesar on 08/05/2014.
 */
@Stateless
public class ValidationLookupRequestDao extends AbstractDao {

    @PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName = "xbrlCheckerDS")
    private EntityManager em;

    public void addValidationLookupRequest(ValidationLookupRequest validationLookupRequest) {
        saveOrUpdate(validationLookupRequest, em);
    }

    public void addValidationAttachmentRequest(ValidationAttachmentRequest validationAttachmentRequest){
        saveOrUpdate(validationAttachmentRequest, em);
    }

    public List<ValidationLookupRequest> getAll() {
        TypedQuery<ValidationLookupRequest> query = em.createNamedQuery("ValidationLookupRequestDao.selectAll", ValidationLookupRequest.class);
        return query.getResultList();
    }

    public List<ValidationLookupRequestCountDto> getLeaderBoardsEntries(int nEntries){
        TypedQuery<ValidationLookupRequestCountDto> query = em.createQuery("select new com.lombardrisk.xbrl.checker.model.ValidationLookupRequestCountDto(vlr.validationId, count(vlr)) " +
                "from com.lombardrisk.xbrl.checker.model.entities.ValidationLookupRequest vlr group by vlr.validationId",
                ValidationLookupRequestCountDto.class);
        List<ValidationLookupRequestCountDto> results = query.getResultList();
        Collections.sort(results, Collections.reverseOrder());
        final int n = Math.min(results.size(), nEntries);
        return results.subList(0, n);
    }

    public int getNumberOfRequest(User user, TimeUnit timeUnit, long amount){
        final Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, (int) -TimeUnit.SECONDS.convert(amount, timeUnit));
        TypedQuery<ValidationAttachmentRequest> query = em.createQuery("select r from ValidationAttachmentRequest r where r.timeStamp > :date and r.user =:user", ValidationAttachmentRequest.class);
        query.setParameter("user", user);
        query.setParameter("date", date);
        return query.getResultList().size();
    }


}
