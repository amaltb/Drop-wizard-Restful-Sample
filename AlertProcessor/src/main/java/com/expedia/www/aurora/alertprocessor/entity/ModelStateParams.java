package com.expedia.www.aurora.alertprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author - _amal
 *
 * Entity representing various model state params at the time of alerts.
 */

@Entity
@Getter
@Setter
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "model_state_id")
    private AlertModelState modelState;


    @Override
    public String toString() {
        return key + "->" + value;
    }
}
