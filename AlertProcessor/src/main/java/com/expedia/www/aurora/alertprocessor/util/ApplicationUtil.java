package com.expedia.www.aurora.alertprocessor.util;

import com.expedia.www.aurora.alertprocessor.entity.AlertInstance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author - _amal
 */

public class ApplicationUtil {

    public static Timestamp validateTimeStamp(Optional<String> ts)
    {
        return ts.map(Timestamp::valueOf).orElseGet(() -> new Timestamp(System.currentTimeMillis()));
    }

    /*public static List<AlertInstance> filterAlertInstances(List<AlertInstance> alerts,
                                                           Map<String, List<String>> requestKeySet)
    {
        final List<AlertInstance> result = new ArrayList<>();
        for (AlertInstance instance:alerts)
        {
            boolean filter = false;
            final Map<String, String> respTemplateKeys = instance.getTemplateKeySet();

            for (final String key: requestKeySet.keySet()) {
                List<String> filterValues = requestKeySet.get(key);
                String responseVal = respTemplateKeys.get(key);
                if(responseVal == null || !filterValues.contains(responseVal))
                {
                    filter = true;
                    break;
                }
            }
            if(!filter) {
                result.add(instance);
            }
        }
        return result;
    }*/
}
