package com.expedia.www.aurora.alertprocessor.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "alertdb.alert_key_set")
public class TemplateKeySet {

    @Id
    @Column(name = "alert_hash_id")
    private String alertHashId;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;
}
