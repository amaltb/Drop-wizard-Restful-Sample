package com.expedia.www.aurora.alertprocessor.util;

import com.codahale.metrics.Timer;
import com.expedia.www.aurora.alertprocessor.entity.*;
import com.expedia.www.aurora.alertprocessor.output.AuroraNotifier;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author - _amal
 *
 * utitlity methods for alert processor application.
 */

public class ApplicationUtil {

    /**
     * converts String to java.sql.timestamp, if no timestamp given return current timestamp.
     *
     * @param ts
     * @return
     */
    public static Timestamp validateTimeStamp(Optional<String> ts) {
        return ts.map(Timestamp::valueOf).orElseGet(() -> new Timestamp(System.currentTimeMillis()));
    }

    /**
     * filter alert instances based on the template key combinations given in the request body.
     *
     * @param alerts
     * @param requestKeySet
     * @return
     */
    public static List<AlertInstance> filterAlertInstances(List<AlertInstance> alerts,
                                                           Map<String, List<String>> requestKeySet) {
        final List<AlertInstance> result = new ArrayList<>();
        for (AlertInstance instance:alerts) {
            boolean filter = false;
            final Set<AlertKeyValue> alertKeyValues = instance.getAlertKeyValues();

            for (final String key: requestKeySet.keySet()) {
                List<String> filterValues = requestKeySet.get(key).stream().map(val -> val.toLowerCase(Locale.ENGLISH))
                        .collect(Collectors.toList());
                for(AlertKeyValue alertKeyValue: alertKeyValues) {
                    if(alertKeyValue.getKey().toString().toLowerCase(Locale.ENGLISH).equals(key.toLowerCase(Locale.ENGLISH))) {
                        if(alertKeyValue.getValue() == null || !filterValues
                                .contains(alertKeyValue.getValue().toString().toLowerCase(Locale.ENGLISH))) {
                            filter = true;
                            break;
                        }
                    }
                }
                if(filter) {
                    break;
                }
            }
            if(!filter) {
                result.add(instance);
            }
        }
        return result;
    }

    /**
     * get global subscription list for all the subscriptions configured for an anomaly template alias.
     *
     * root: create a notification_subscription object
     * for each alertInstance
     * 1. get alert_type
     * 2. get alert_subscript set
     * 3. for each subscription
     *  3.1 check if it is disabled
     *  3.2 if not compare alert types
     *  3.3 if matches get all subscription_channel values
     *  3.4 for_each subscription channel execute
     *      3.4.1 case email: parse and add all emails to notification_subscription
     *            case sns: parse and add all sns topic to notification_subscription
     *            case slack: parse and add all slack topic to notification_subscription
     */
    public static NotificationSubscription getGlobalSubscriptionList(List<AlertInstance> alertInstances)
    {
        final NotificationSubscription subscription = new NotificationSubscription();
        for (final AlertInstance instance: alertInstances) {
            final String alertType = instance.getAlertType().getAlertTypeName();
            final Set<AlertSubscription> subscriptions = instance.getAlertSubscriptions();

            for (final AlertSubscription alertSubscription: subscriptions) {
                if(alertSubscription.getDisabled() || !getAlertTypesSubscribed(alertSubscription).contains(alertType)){
                    continue;
                }
                else{
                    for (final AlertSubscriptionChannel channel: alertSubscription.getSubscriptionChannels()) {
                        switch (channel.getKey().toLowerCase(Locale.ENGLISH)) {
                            case "email":
                                final String[] emails = channel.getValue()
                                        .replace("[", "")
                                        .replace("]", "")
                                        .split(",");
                                subscription.addAllToEmail(Arrays.asList(emails));
                                break;
                            case "sns":
                                final String[] snsTopics = channel.getValue()
                                        .replace("[", "")
                                        .replace("]", "")
                                        .split(",");
                                subscription.addAllToEmail(Arrays.asList(snsTopics));
                                break;
                            default:
                                break;

                        }
                    }
                }
            }
        }
        return subscription;
    }

    /**
     * generate alert report incorporating all alert instances for the given anomaly template alias in given time period.
     * format:
     * There are <number of alerts> alerts raised for the template <template alias name> during the period <start time>
     * to <end time>. Details are attached below
     *
     * Anomaly Template Alias: <>
     * Time Period: <from> - <to>
     * Filter Criteria: <filter key set>
     *
     * CreatedOn	|	AlertModelState	|	AlertKeySet
     * <timestamp>		<alert model state>	|	[alert key values]
     *
     * @param templateAliasName
     * @param st
     * @param et
     * @param requestKeySet
     * @param alertInstances
     * @return
     */
    public static String generateReport(String templateAliasName, Timestamp st, Timestamp et,
                                        Map<String, List<String>> requestKeySet, List<AlertInstance> alertInstances)
    {
        final String reportTemplate = "There are %d alerts raised for the template %s during the period %s to %s. " +
                "Details are attached below\n\n" +
                "Anomaly Template .Alias: %s\nTime Period: %s to %s\nFilter Criteria: %s\n\n" +
                "CreatedOn\t|\tAlertModelState\t|\tAlertKeySet\n";

        final StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format(reportTemplate, templateAliasName, st.toString(), et.toString(),
                templateAliasName, st.toString(), et.toString(), requestKeySet));

        for (final AlertInstance instance: alertInstances) {
            final Timestamp createdOn = instance.getCreatedOn();
            final AlertModelState modelState = instance.getAlertModelState();
            final AlertKeyValue keyValue = instance.getAlertKeyValues();

            reportBuilder
                    .append(createdOn)
                    .append("\t\t")
                    .append(modelState)
                    .append("\t\t")
                    .append(keyValue)
                    .append("\n");
        }
        return reportBuilder.toString();
    }


    /**
     * call notify method to send respective notifications to subscription channels. Currently support email and sns
     *
     * @param report
     * @param subscription
     * @param notifiers
     */
    public static void notify(String report, NotificationSubscription subscription, List<AuroraNotifier> notifiers)
    {
        for (final AuroraNotifier notifier: notifiers) {
            final Timer.Context notifyCtxt = ServiceMetrics.getRegistry()
                    .timer("report."+notifier.getName()+".notifyTime").time();
            notifier.notifyAlert(subscription, report);
            notifyCtxt.close();
        }
    }

    /**
     * get all the alert_types subscribed in an alert subscription configuration.
     *
     * @param subscription
     * @return
     */
    private static List<String> getAlertTypesSubscribed(AlertSubscription subscription)
    {
        if(subscription.getAlertTypes() != null)
        {
            return Arrays.asList(subscription.getAlertTypes()
                    .replace("[", "")
                    .replace("]", "")
                    .split(","));
        }
        else
        {
            return Collections.EMPTY_LIST;
        }

    }
}
