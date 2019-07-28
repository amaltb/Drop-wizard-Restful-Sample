package com.expedia.www.aurora.alertprocessor.output;


import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.codahale.metrics.Meter;
import com.expedia.www.aurora.alertprocessor.processor.AuroraRequestExecutor;
import com.expedia.www.aurora.alertprocessor.processor.AuroraTaskTemplate;
import com.expedia.www.aurora.alertprocessor.util.NotificationSubscription;
import com.expedia.www.aurora.alertprocessor.util.ServiceMetrics;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * From AuroraNotifier repo
 *
 * To send alert report over amazon simple email service.
 */

public class AuroraEmailNotifier implements AuroraNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuroraEmailNotifier.class);
    private final AmazonSimpleEmailService client;
    private final Meter MessagePushToEmailFailedMeter;
    private final Meter MessagePushToEmailMeter;
    private final Meter MessagePushToEmailSuccessMeter;
    private final AuroraRequestExecutor auroraRequestExecutor;

    public AuroraEmailNotifier(final AmazonSimpleEmailService client, AuroraRequestExecutor executor) {
        assert client != null;
        this.client = client;
        this.MessagePushToEmailFailedMeter = ServiceMetrics.getRegistry().meter("notifier.email.failed");
        this.MessagePushToEmailMeter = ServiceMetrics.getRegistry().meter("notifier.email.sent");
        this.MessagePushToEmailSuccessMeter = ServiceMetrics.getRegistry().meter("notifier.email.success");
        this.auroraRequestExecutor = executor;
    }

    @Override
    public void notifyAlert(NotificationSubscription subscription, String message) {
        if (subscription.getEmailIds() == null || subscription.getEmailIds().size() == 0) {
            return;
        }
        final List<String> emailIds = subscription.getEmailIds().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (!emailIds.isEmpty()) {
            MessagePushToEmailMeter.mark();
            final Destination destination = new Destination().withToAddresses(emailIds);
            final Content subject = new Content().withData("Aurora alert report");

            final Content htmlBody = new Content().withData(message);
            final Body body = new Body().withHtml(htmlBody);
            final Message emailMessage = new Message().withSubject(subject).withBody(body);
            final SendEmailRequest request = new SendEmailRequest().withSource("AuroraNotifications@expedia.com").withDestination(destination)
                    .withMessage(emailMessage).withReplyToAddresses("noreply@expedia.com");
            final AuroraTaskTemplate mainTaskCallable = new AuroraTaskTemplate() {
                @Override
                public void executeTask() {
                    LOGGER.debug("Sending Email "+ message);
                    client.sendEmail(request);
                    MessagePushToEmailSuccessMeter.mark();
                }

                @Override
                public void logException(Exception ex) {
                    MessagePushToEmailFailedMeter.mark();
                    LOGGER.error("Error sending email => to " + destination, ex);
                }
            };
            auroraRequestExecutor.executeTask(mainTaskCallable);
        }
    }

    @Override
    public String getName() {
        return "email";
    }
}

