package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "alertdb.alert_type")
@NamedQueries({
        @NamedQuery(name = "com.ab.example.dropwizardsample.entity.AlertType.findAll",
                query = "select a from AlertType a")
})
@Data
public class AlertType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;

    @Column(name = "alert_type_name", unique = true)
    private String alertTypeName;

    @OneToMany(mappedBy = "alertType")
    private Set<AlertInstance> instances;
}
