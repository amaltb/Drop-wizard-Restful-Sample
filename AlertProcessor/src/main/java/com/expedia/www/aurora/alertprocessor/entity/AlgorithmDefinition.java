package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@Table(name = "alertdb.algo_definition")
public class AlgorithmDefinition implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "algo_name")
    private String algoName;

    @Column(name = "alias_name")
    private String aliasName;

    @OneToMany(mappedBy = "algorithmDefinition")
    private Set<AlertInstance> instances;

    @OneToMany(mappedBy = "definition", cascade = CascadeType.ALL)
    private Set<AlgoParameter> algoParameters;
}
