package com.lombardrisk.xbrl.checker.model.entities;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by Cesar on 09/05/2014.
 */
@Entity()
@Table(name = "USER_EMAIL_VALIDATION_REQUEST")
@NamedQueries({
        @NamedQuery(name = "UserEmailValidationRequest.findByEmailHash",
                query = "select r from UserEmailValidationRequest r where  r.emailAddressHash = :emailHash")
}
)
public class UserEmailValidationRequest implements JpaEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @Column(name = "EMAIL_ADDRESS_HASH")
    private int emailAddressHash;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "REQUEST_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar requestTime;

    public UserEmailValidationRequest() {
    }

    public UserEmailValidationRequest(String emailAddress, int emailAddressHash, String token, Calendar requestTime) {
        this.emailAddress = emailAddress;
        this.emailAddressHash = emailAddressHash;
        this.token = token;
        this.requestTime = requestTime;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getEmailAddressHash() {
        return emailAddressHash;
    }

    public void setEmailAddressHash(int emailAddressHash) {
        this.emailAddressHash = emailAddressHash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public Calendar getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Calendar requestTime) {
        this.requestTime = requestTime;
    }
}
