package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

/**
 * @author - _amal
 *
 * Entity class represnting an alert instance
 */

@Entity
@Table(name = "alertdb.alert_instance")
@NamedQueries({
        @NamedQuery(name = AlertInstance.FIND_BY_TEMPLATE_NAME,
                query = AlertInstance.FIND_BY_TEMPLATE_QUERY)
})
@Getter
@Setter
public class AlertInstance implements Serializable {

    public static final String FIND_BY_TEMPLATE_NAME = "com.ab.example.dropwizardsample.entity.AlertInstance.findByTemplate";
    static final String FIND_BY_TEMPLATE_QUERY = "select a from AlertInstance a " +
            "where a.aliasName = :templateAliasName and a.createdOn between :startTime and :endTime";


    @Id
    @Column(name = "alert_hash_id")
    private String alertHashId;

    @Column(name = "alert_value")
    private Double alertValue;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "alias_name")
    private String aliasName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "alert_type_id")
    private AlertType alertType;

    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL)
    private Set<AlertKeyValue> alertKeyValues;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "algo_id")
    private AlgorithmDefinition algorithmDefinition;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "model_state_id")
    private AlertModelState alertModelState;

    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL)
    private Set<AlertSubscription> alertSubscriptions;

}
