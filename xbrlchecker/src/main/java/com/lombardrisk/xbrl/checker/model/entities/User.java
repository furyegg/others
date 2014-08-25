package com.lombardrisk.xbrl.checker.model.entities;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by Cesar on 09/05/2014.
 */
@Entity
@Table(name = "USERS")
@NamedQueries({
        @NamedQuery(name = "User.findByEmail", query = "select u from User u where u.emailAddress = :emailAddress")
})
public class User implements JpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDINATE_ID")
    private Long id;

    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<ValidationLookupRequest> validationLookupRequests;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<ValidationAttachmentRequest> validationAttachmentRequests;

    public User() {
    }

    public User(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Long getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Set<ValidationLookupRequest> getValidationLookupRequests() {
        return validationLookupRequests;
    }

    public Set<ValidationAttachmentRequest> getValidationAttachmentRequests() {
        return validationAttachmentRequests;
    }
}
