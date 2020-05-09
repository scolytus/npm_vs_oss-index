package io.github.scolytus.npmvsoss;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("App started");
        final Stopwatch stopwatch = Stopwatch.createStarted();

        if (args.length < 1) {
            LOGGER.error("specify step");
        } else if ("Step1".equalsIgnoreCase(args[0])) {
            LOGGER.info("Executing Step 1");
            new Step1().run();
        } else if ("Step2".equalsIgnoreCase(args[0])) {
            LOGGER.info("Executing Step 2");
            new Step2().run();
        } else if ("Step3".equalsIgnoreCase(args[0])) {
            LOGGER.info("Executing Step 3");
            new Step3().run();
        } else if ("Step4".equalsIgnoreCase(args[0])) {
            LOGGER.info("Executing Step 4");
            new Step4().run();
        } else if ("Step4a".equalsIgnoreCase(args[0])) {
            LOGGER.info("Executing Step 4a");
            //new Step4a().run();
        } else if ("Step5".equalsIgnoreCase(args[0])) {
            LOGGER.info("Executing Step 5");
            new Step5().run();
        } else {
            LOGGER.error("specify step");
        }

        LOGGER.info("DONE after {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

}
