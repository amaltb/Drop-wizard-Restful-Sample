package com.expedia.www.aurora.alertprocessor.entity;

import javax.persistence.*;

@Entity
@Table(name = "alertdb.algo_params")
public class AlgoParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String val;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "algo_id")
    private AlgorithmDefinition definition;
}
