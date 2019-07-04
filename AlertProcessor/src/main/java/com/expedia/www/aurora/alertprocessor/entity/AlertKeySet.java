package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "alertdb.alert_key_set")
public class AlertKeySet {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "alert_hash_id")
    private AlertInstance instance;
}
