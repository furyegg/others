package com.lombardrisk.xbrl.render.model.entities;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.lombardrisk.xbrl.checker.model.entities.JpaEntity;
import com.lombardrisk.xbrl.render.util.UUIDUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by Cesar on 06/06/2014.
 */

@Entity
@Table(name = "USER_TOKEN")
public class UserToken implements JpaEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Version
    @Column(name = "DATABASE_VERSION_ID")
    private Long databaseVersionId;

    @Column(name = "token")
    private String token;

    @Column(name = "ORIGINAL_REQUESTS")
    private long originalRequests;

    @Column(name = "REMAINING_REQUESTS")
    private long remainingRequests;

    @Column(name = "DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateCreated;

    @Column(name = "LAST_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastUpdated;

    @Column(name = "PAYMENT_REFERENCE")
    private String paymentReference;

    @Transient
    private String humanReadableToken;

    public Long getId() {
        return id;
    }

    public Long getDatabaseVersionId() {
        return databaseVersionId;
    }

    public void setDatabaseVersionId(Long databaseVersionId) {
        this.databaseVersionId = databaseVersionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        this.humanReadableToken = UUIDUtils.humanReadableUUID(token);
    }

    public long getOriginalRequests() {
        return originalRequests;
    }

    public void setOriginalRequests(long originalRequests) {
        this.originalRequests = originalRequests;
    }

    public long getRemainingRequests() {
        return remainingRequests;
    }

    public void setRemainingRequests(long remainingRequests) {
        this.remainingRequests = remainingRequests;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Calendar getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Calendar lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getHumanReadableToken() {
        return humanReadableToken;
    }



    public static UserToken createUserToken(long numberOfRequest) {
        UserToken userToken = new UserToken();
        Calendar now = Calendar.getInstance();
        userToken.setDateCreated(now);
        userToken.setLastUpdated(now);
        userToken.setOriginalRequests(numberOfRequest);
        userToken.setRemainingRequests(numberOfRequest);
        userToken.setToken(UUIDUtils.smallHexUUID());
        return userToken;
    }
}
