package com.expedia.www.aurora.alertprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * @author - _amal
 *
 * Entity representing present model state at the time of alert.
 */

@Entity
@Getter
@Setter
@Table(name = "alertdb.alert_model_state")
public class AlertModelState {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @OneToOne(mappedBy = "alertModelState")
    private AlertInstance alertInstance;

    @OneToMany(mappedBy = "modelState")
    private Set<ModelStateParams> params;

    @Override
    public String toString() {
        return params.toString();
    }
}
