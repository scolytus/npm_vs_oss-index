package io.github.scolytus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import io.github.scolytus.data.AllData;
import io.github.scolytus.data.Counter;
import io.github.scolytus.data.Finding;
import io.github.scolytus.data.PackageVersion;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final int MAX_ADVISORY = 1513;

    private static final int SLEEP_NORMAL = 2;

    private static final int SLEEP_LIMIT_HIT = 90;

    public static final String BASE_URL = "https://www.npmjs.com/advisories/";

    private AllData allData = new AllData();

    private List<Integer> skipped = allData.getSkipped();

    private List<Finding> allFindings = allData.getAllFindings();

    private Map<String, Counter> errors = allData.getErrors();

    private List<Integer> retry = allData.getRetry();

    // I hate it when I do this... damn you Set interface
    private Map<String, PackageVersion> packageVersions = allData.getPackageVersions();

    private List<Integer> error404 = allData.getError404();

    private List<Integer> error429 = allData.getError429();

    private List<Integer> error5xx = allData.getError5xx();

    public static void main(String[] args) throws IOException {
        LOGGER.info("App started");
        final Stopwatch stopwatch = Stopwatch.createStarted();

        if (args.length < 1) {
            LOGGER.error("specify step");
        } else if ("Step1".equalsIgnoreCase(args[0])) {
            LOGGER.info("Executing Step 1");
            new App().run();
        } else if ("Step2".equalsIgnoreCase(args[0])) {
            LOGGER.info("Executing Step 2");
            new Step2().run();
        } else {
            LOGGER.error("specify step");
        }

        LOGGER.info("DONE after {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public void run() {
        initRetry();

        int iterationCount = 1;
        List<Integer> currentRetry;
        do {
            currentRetry = new ArrayList<>(retry);
            retry.clear();

            LOGGER.info("Start iteration {}, processing {} advisories", iterationCount, currentRetry.size());

            for (Integer advisory : currentRetry) {
                run(advisory);
            }
        } while (currentRetry.size() > 0);

        analyzeFindings();
        findDups();

        LOGGER.info("Skipped {} advisories", skipped.size());
        LOGGER.info("Found {} unique package@version", packageVersions.size());

        writeAllData();

        LOGGER.info("Data written");
    }

    private void run(final int advisory) {
        try {
            process(advisory);
        } catch (HttpStatusException e) {
            final int statusCode = e.getStatusCode();
            final String statusCodeString = String.valueOf(statusCode);

            LOGGER.error("HTTP status error for advisory '{}': {}", advisory, statusCode);

            errors.computeIfAbsent(statusCodeString, k -> new Counter()).inc();

            if (statusCode == 429) {
                do429(advisory);
            }

            if (statusCode >= 500) {
                do5xx(advisory);
            }

            if (statusCode == 404) {
                do404(advisory);
            }
        } catch (IOException e) {
            LOGGER.error("Error for advisory '{}'", advisory, e);
        }

        sleepRateLimit();
    }

    private void process(final int advisory) throws IOException {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final String versionUrl = getVersionUrl(advisory);

        final Document doc = Jsoup.connect(versionUrl).get();

        final Elements dls = doc.select("dl");
        final Elements packageNameElements = doc.select("h1 + a");

        if (dls.size() != 2) {
            LOGGER.warn("Unexpected: dls.size() != 2 [is: {}] for '{}' - skipping", dls.size(), advisory);
            skip(advisory);
            return;
        }

        if (packageNameElements.size() != 1) {
            LOGGER.warn("Unexpected: packageNameElements.size() != 1 [is: {}] for '{}' - skipping", packageNameElements.size(), advisory);
            skip(advisory);
            return;
        }

        final String packageName = packageNameElements.get(0).text();
        final Element affected = dls.get(0);
        final Element unaffected = dls.get(1);

        LOGGER.debug("Processing advisory '{}' - '{}'", advisory, packageName);

        allFindings.addAll(getFindings(affected, packageName, advisory, true));
        allFindings.addAll(getFindings(unaffected, packageName, advisory, false));

        LOGGER.debug("DONE with '{}' after {}ms", advisory, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    private void analyzeFindings() {
        allFindings.forEach(f -> {
            final String packageVersionString = PackageVersion.toString(f);

            final PackageVersion packageVersion =
                    packageVersions.computeIfAbsent(packageVersionString, k -> new PackageVersion(f.packageName, f.version));

            packageVersion.add(f);
        });
    }

    private void findDups() {
        int max = 1;
        PackageVersion found = null;
        for (String packageVersion : packageVersions.keySet()) {
            if (packageVersions.get(packageVersion).getFindings().size() > max) {
                found = packageVersions.get(packageVersion);
                max = found.getFindings().size();
            }
        }

        LOGGER.info("Most vulnerable package@version: '{}': {}", found, max);
    }

    private List<Finding> getFindings(final Element dl, final String packageName, final int advisory, final boolean affected) {
        final List<Finding> findings = new ArrayList<>();

        final Elements versions = dl.select("dt");
        for (Element version : versions) {
            findings.add(new Finding(packageName, version.text(), advisory, affected));
        }

        return findings;
    }

    private void sleep429() {
        sleepSeconds(SLEEP_LIMIT_HIT);
    }

    private void sleepRateLimit() {
        sleepSeconds(SLEEP_NORMAL);
    }

    private void sleepSeconds(final int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException e) {
            LOGGER.error("sleep failed?", e);
        }
    }

    private void writeAllData() {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("step1.json").toFile(), allData);
        } catch (IOException e) {
            LOGGER.info("Something went wrong");
        }
    }

    private synchronized void skip(final int advisory) {
        skipped.add(advisory);
    }

    private void initRetry() {
        for (int i = 1; i <= MAX_ADVISORY; i++) {
            retry.add(i);
        }
    }

    private void do404(final int advisory) {
        error404.add(advisory);
    }

    private void do429(final int advisory) {
        retry.add(advisory);
        sleep429();
        error429.add(advisory);
    }

    private void do5xx(final int advisory) {
        if (!error5xx.contains(advisory)) {
            retry.add(advisory);
            error5xx.add(advisory);
        } else {
            LOGGER.warn("Advisory '{}' already in list of 5xx errors - skipping", advisory);
        }
    }

    protected static String getVersionUrl(final int advisory) {
        return BASE_URL + advisory + "/versions";
    }
}
