package com.expedia.www.aurora.alertprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author - _amal
 *
 * Entity represnting alert_type dimension.
 */

@Entity
@Table(name = "alertdb.alert_type")
@Getter
@Setter
public class AlertType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;

    @Column(name = "alert_type_name", unique = true)
    private String alertTypeName;

    @JsonIgnore
    @OneToMany(mappedBy = "alertType")
    private Set<AlertInstance> instances;
}
