package com.lombardrisk.xbrl.render.model.entities;

import com.lombardrisk.xbrl.checker.model.entities.JpaEntity;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by Cesar on 06/06/2014.
 */
@Entity
@Table(name = "USER_TOKEN_USAGE")
public class UserTokenUsage implements JpaEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_TOKEN")
    private UserToken userToken;

    @Column(name="TIME_USED")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar timeUsed;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    public UserToken getUserToken() {
        return userToken;
    }

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

    public Calendar getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(Calendar timeUsed) {
        this.timeUsed = timeUsed;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getId() {
        return id;
    }
}
