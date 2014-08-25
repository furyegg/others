package com.lombardrisk.xbrl.checker.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Cesar on 08/05/2014.
 */

@Entity
@Table(name = "VALIDATION_LOOKUP_REQUEST")
@NamedQueries({
        @NamedQuery(name = "ValidationLookupRequestDao.selectAll", query = "select r from ValidationLookupRequest r")
}
)
public class ValidationLookupRequest implements Serializable, JpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "VALIDATION_ID")
    private String validationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER")
    private User user;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @Column(name = "TIME_STAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar timeStamp;

    public Long getId() {
        return id;
    }

    public String getValidationId() {
        return validationId;
    }

    public void setValidationId(String validationId) {
        this.validationId = validationId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Calendar getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }
}
