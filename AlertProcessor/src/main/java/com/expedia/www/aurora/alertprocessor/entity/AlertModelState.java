package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "alertdb.alert_model_state")
public class AlertModelState {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "alertModelState")
    private AlertInstance alertInstance;

    @OneToMany(mappedBy = "modelState")
    private Set<ModelStateParams> params;
}
