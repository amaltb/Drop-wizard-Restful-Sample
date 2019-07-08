package com.expedia.www.aurora.alertprocessor.processor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * From Aurora-Notifier repo.
 *
 * executor for sending notifications.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class AuroraRequestExecutor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuroraRequestExecutor.class);
    final private int maxRetryCount;
    final private ExecutorService executorService;
    final private DelayQueue<AuroraNotificationTask> delayQueue = new DelayQueue<>();
    private boolean continueProcessing = true;

    public AuroraRequestExecutor(final int maxRetryCount) {
        executorService = Executors.newFixedThreadPool(2);
        this.maxRetryCount = maxRetryCount;
    }

    public void executeTask(final AuroraTaskTemplate auroraTaskTemplate) {
        final AuroraNotificationTask notificationTask = new AuroraNotificationTask(auroraTaskTemplate, maxRetryCount);
        tryAndExecute(notificationTask);
    }

    private void tryAndExecute(AuroraNotificationTask task) {
        executorService.submit(() -> {
            try {
                if (!task.execute()) {
                    task.addOneMinuteDelay();
                    delayQueue.offer(task);
                }
            } catch (Exception e) {
                LOGGER.error("Error encountered in running task queue", e);
            }
        });
    }

    public void run() {
        while (continueProcessing) {
            // poll takes care of empty list and if delay is not expired for any element
            final AuroraNotificationTask auroraNotificationTask = delayQueue.poll();
            if (auroraNotificationTask == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.warn("Sleep interrupted", e);
                }
            } else {
                tryAndExecute(auroraNotificationTask);
            }
        }
    }

    public void stop() {
        this.continueProcessing = false;
    }
}

