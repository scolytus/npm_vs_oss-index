package io.github.scolytus.npmvsoss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStep.class);

    protected static final int SLEEP_NORMAL = 2;

    protected static final int SLEEP_LIMIT_HIT = 90;


    protected void sleep429() {
        sleepSeconds(SLEEP_LIMIT_HIT);
    }

    protected void sleepRateLimit() {
        sleepSeconds(SLEEP_NORMAL);
    }

    protected void sleepSeconds(final int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException e) {
            LOGGER.error("sleep failed?", e);
        }
    }

}
