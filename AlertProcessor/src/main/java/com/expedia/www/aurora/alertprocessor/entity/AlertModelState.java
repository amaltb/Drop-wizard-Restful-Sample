package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "alertdb.alert_model_state")
public class AlertModelState {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "alert_hash_id")
    private AlertInstance alertInstance;
}
