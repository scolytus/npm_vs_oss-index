package io.github.scolytus;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("App started");
        final Stopwatch stopwatch = Stopwatch.createStarted();

        LOGGER.info("DONE after {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}
