package com.expedia.www.aurora.alertprocessor.util;

import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author - _amal
 *
 * representing alert report subscription.
 */

@Data
public class NotificationSubscription {
    private Set<String> emailIds;
    private Set<String> snsTopicArns;

    public NotificationSubscription() {
        emailIds = new HashSet<>();
        snsTopicArns = new HashSet<>();
    }

    public void addToEmail(String emailId)
    {
        emailIds.add(emailId);
    }

    public void addAllToEmail(Collection<String> emailIdCollection)
    {
        emailIds.addAll(emailIdCollection);
    }

    public void addToSns(String snsTopicArn)
    {
        snsTopicArns.add(snsTopicArn);
    }

    public void addAllToSns(Collection<String> snsTopicArnCollection)
    {
        snsTopicArns.addAll(snsTopicArnCollection);
    }
}
