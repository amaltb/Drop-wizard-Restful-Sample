package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "alertdb.alert_model_state_params")
public class ModelStateParams {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "model_state_id")
    private AlertModelState modelState;
}
