package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "alertdb.alert_subscription")
public class AlertSubscription {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "message_text")
    private String messageText;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "alert_hash_id")
    private AlertInstance instance;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private Set<AlertSubscriptionChannel> subscriptionChannels;
}
