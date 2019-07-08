package com.expedia.www.aurora.alertprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * @author - _amal
 *
 * Entity representing alert subscription details.
 */

@Entity
@Getter
@Setter
@Table(name = "alertdb.alert_subscription")
public class AlertSubscription {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "disabled")
    private boolean disabled;

    @Column(name = "alert_types")
    private String alertTypes;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "alert_hash_id")
    private AlertInstance instance;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private Set<AlertSubscriptionChannel> subscriptionChannels;
}
