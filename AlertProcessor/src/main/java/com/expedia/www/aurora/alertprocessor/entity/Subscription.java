package com.expedia.www.aurora.alertprocessor.entity;

import com.expedia.www.aurora.alertprocessor.util.StringArrayType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@TypeDefs({@TypeDef(name = "string-array", typeClass = StringArrayType.class)})
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "template_alias_name")
    private String templateAliasName;

    @Column(name = "message_text")
    private String messageText;

    @Type( type = "string-array" )
    @Column(name = "alert_types")
    private String[] alertTypes;

    @Type( type = "string-array" )
    @Column(name = "sns_topic_arn")
    private String[] snsTopicArns;

    @Type( type = "string-array" )
    @Column(name = "email_ids")
    private String[] emailIds;

    @Type( type = "string-array" )
    @Column(name = "slackChannelIds")
    private String[] slackChannelIds;
}
