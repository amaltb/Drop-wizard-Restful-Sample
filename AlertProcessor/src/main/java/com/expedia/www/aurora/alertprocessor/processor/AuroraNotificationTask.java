package com.expedia.www.aurora.alertprocessor.processor;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * From Aurora-Notifier repo.
 *
 * defines a notification task with retries.
 */

public class AuroraNotificationTask implements Delayed {

    final private AuroraTaskTemplate taskTemplate;
    private int retriesLeft;
    private long timeout;

    AuroraNotificationTask(AuroraTaskTemplate r, int retries) {
        this.taskTemplate = r;
        this.retriesLeft = retries;
        this.timeout = System.currentTimeMillis();
    }

    final void addOneMinuteDelay() {
        timeout = System.currentTimeMillis() + 60000L;
    }


    Boolean execute() throws Exception {
        try {
            this.taskTemplate.executeTask();
            return Boolean.TRUE;
        } catch (Exception e) {
            if (retriesLeft > 0) {
                this.retriesLeft--;
                this.taskTemplate.logException(e);
            } else {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(timeout - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == null) {
            return 1;
        }
        return Long.signum(getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
