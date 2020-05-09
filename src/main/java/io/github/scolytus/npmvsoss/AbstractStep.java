package io.github.scolytus.npmvsoss;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.scolytus.npmvsoss.data.AllData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public abstract class AbstractStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStep.class);

    protected static final int SLEEP_NORMAL = 2;

    protected static final int SLEEP_LIMIT_HIT = 90;

    protected AllData allData = null;

    public abstract void run();

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

    protected void initAllData(final String fileName) {
        allData = load(fileName, AllData.class);
    }

    public void writeData(final String fileName, final Object payload) {
        // Don't re-use, this is not a long running server app
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(fileName).toFile(), payload);
        } catch (IOException e) {
            LOGGER.info("Something went wrong");
        }
    }

    public <T> T load(final String fileName, final Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(Paths.get(fileName).toFile(), clazz);
        } catch (IOException e) {
            LOGGER.error("Can't read Data", e);
            throw new IllegalStateException("Can't read Data", e);
        }
    }

}
