package com.lombardrisk.xbrl.checker.model.entities;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by Cesar on 14/05/2014.
 */
@Entity
@Table(name = "VALIDATION_ATTACHMENT_REQUEST")
public class ValidationAttachmentRequest implements  JpaEntity{

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

    public void setValidationId(String validationId) {
        this.validationId = validationId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setTimeStamp(Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getValidationId() {
        return validationId;
    }

    public User getUser() {
        return user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Calendar getTimeStamp() {
        return timeStamp;
    }
}
