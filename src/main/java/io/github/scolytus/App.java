package io.github.scolytus;

import com.google.common.base.Stopwatch;
import io.github.scolytus.data.Finding;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static final String BASE_URL= "https://www.npmjs.com/advisories/";

    private List<Integer> skipped = new ArrayList<>();

    private List<Finding> allFindings = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        LOGGER.info("App started");
        final Stopwatch stopwatch = Stopwatch.createStarted();

        new App().run();

        LOGGER.info("DONE after {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public void run() {
        int advisoryCounter = 0;
        for (int advisory = 1; advisory < 5; advisory++) {
            try {
                process(advisory);
                advisoryCounter++;
            } catch (IOException e) {
                LOGGER.error("Error for advisory '{}'", advisory, e);
            }
        }

        LOGGER.info("Found {} findings from {} advisories", allFindings.size(), advisoryCounter);
        LOGGER.info("Skipped {} advisories", skipped.size());
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

    private List<Finding> getFindings(final Element dl, final String packageName, final int advisory, final boolean affected) {
        final List<Finding> findings = new ArrayList<>();

        final Elements versions = dl.select("dt");
        for (Element version : versions) {
            findings.add(new Finding(packageName, version.text(), advisory, affected));
        }

        return findings;
    }

    private synchronized void skip(final int advisory) {
        skipped.add(advisory);
    }

    protected static String getVersionUrl(final int advisory) {
        return BASE_URL + advisory + "/versions";
    }
}
