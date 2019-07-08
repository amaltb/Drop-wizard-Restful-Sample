package com.expedia.www.aurora.alertprocessor.output;

import com.expedia.www.aurora.alertprocessor.util.NotificationSubscription;

/**
 * Taken from Aurora-Notifier repo.
 *
 * notifer interface. see AuroraEmailNotifier & AuroraSNSNotifier for implementations.
 */
public interface AuroraNotifier {
    void notifyAlert(NotificationSubscription subscription, String message);
    String getName();
}
