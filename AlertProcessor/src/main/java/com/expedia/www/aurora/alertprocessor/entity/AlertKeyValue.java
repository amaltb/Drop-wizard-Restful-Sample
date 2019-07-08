package com.expedia.www.aurora.alertprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author - _amal
 *
 * Entity representing ticker key -> value corresponding to an alert insatnce.
 */

@Entity
@Getter
@Setter
@Table(name = "alertdb.alert_key_set")
public class AlertKeyValue {

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
    @JoinColumn(name = "alert_hash_id")
    private AlertInstance instance;

    @Override
    public String toString() {
        return key + "->" + value;
    }
}
