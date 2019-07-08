package com.expedia.www.aurora.alertprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author - _amal
 *
 * Entity representing alert algorithm parameters.
 */

@Entity
@Getter
@Setter
@Table(name = "alertdb.algo_params")
public class AlgoParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String val;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "algo_id")
    private AlgorithmDefinition definition;
}
