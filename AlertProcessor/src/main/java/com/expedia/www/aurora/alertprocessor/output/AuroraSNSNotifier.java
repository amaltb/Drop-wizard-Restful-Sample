package com.expedia.www.aurora.alertprocessor.output;


import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.codahale.metrics.Meter;

import com.expedia.www.aurora.alertprocessor.processor.AuroraRequestExecutor;
import com.expedia.www.aurora.alertprocessor.processor.AuroraTaskTemplate;
import com.expedia.www.aurora.alertprocessor.util.NotificationSubscription;
import com.expedia.www.aurora.alertprocessor.util.ServiceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Taken from Aurora-Notifier repo
 *
 * To send alert report over amazon simple notification service.
 */

public class AuroraSNSNotifier implements AuroraNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuroraSNSNotifier.class);
    private final Meter MessagePushToSnsFailedMeter;
    private final Meter MessagePushToSnsSuccessMeter;
    private final Meter MessagePushToSnsMeter;
    private final AuroraRequestExecutor auroraRequestExecutor;
    private final ConcurrentHashMap<Region, AmazonSNS> snsClientMap = new ConcurrentHashMap<>();

    public AuroraSNSNotifier(final AuroraRequestExecutor executor) {
        this.MessagePushToSnsFailedMeter = ServiceMetrics.getRegistry().meter("notifier.sns.failed");
        this.MessagePushToSnsMeter = ServiceMetrics.getRegistry().meter("notifier.sns.sent");
        this.MessagePushToSnsSuccessMeter = ServiceMetrics.getRegistry().meter("notifier.sns.success");
        this.auroraRequestExecutor = executor;
    }

    @Override
    public void notifyAlert(NotificationSubscription subscription, String message) {

        if (subscription.getSnsTopicArns() != null && subscription.getSnsTopicArns().size() > 0) {
            for (final String snsTopic : subscription.getSnsTopicArns()) {

                if (snsTopic.isEmpty()) {
                    continue;
                }

                MessagePushToSnsMeter.mark();
                final PublishRequest publishRequest = new PublishRequest(snsTopic, message);

                final AuroraTaskTemplate mainTaskCallable = new AuroraTaskTemplate() {
                    @Override
                    public void executeTask() {
                            AmazonSNS snsClient = snsClientMap.computeIfAbsent(getRegionFromSNSTopic(snsTopic), (region) ->
                                    AmazonSNSClient.builder().withRegion(region.getName()).build());
                            snsClient.publish(publishRequest);
                            MessagePushToSnsSuccessMeter.mark();
                    }

                    @Override
                    public void logException(Exception ex) {
                        MessagePushToSnsFailedMeter.mark();
                        LOGGER.error("Error sending SNS for topic={} with error={} ", snsTopic, ex.getMessage(), ex);
                    }
                };
                auroraRequestExecutor.executeTask(mainTaskCallable);
            }
        }
    }

    @Override
    public String getName() {
        return "sns";
    }

    private Region getRegionFromSNSTopic(String snsTopic){
        final String[] arnComponents = snsTopic.split(":");
        return Region.getRegion(Regions.fromName(arnComponents[3]));
    }
}

