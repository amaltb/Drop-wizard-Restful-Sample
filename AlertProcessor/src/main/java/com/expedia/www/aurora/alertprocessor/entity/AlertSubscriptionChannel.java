package com.expedia.www.aurora.alertprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author - _amal
 *
 * Entity representng subscription channel associated with alert subscription.
 */

@Entity
@Getter
@Setter
@Table(name = "alertdb.alert_subscription_channel")
public class AlertSubscriptionChannel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "subscription_id")
    private AlertSubscription subscription;


    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
