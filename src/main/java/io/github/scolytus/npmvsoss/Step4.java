package io.github.scolytus.npmvsoss;

import io.github.scolytus.npmvsoss.data.PackageVersion;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Step4 extends AbstractStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(Step3.class);

    public Step4() {
        initAllData("step2.allData.json");
    }

    @Override
    public void run() {
        final Set<String> allReferences = allData.getPackageVersions().values().stream()
                .map(PackageVersion::getOssIndexReport)
                .flatMap(r -> r.getVulnerabilities().stream())
                .map(v -> v.get("reference"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toSet());

        LOGGER.info("Number of unique references: {}", allReferences.size());

        final Map<String, List<URL>> results = allReferences.stream()
                .collect(Collectors.toMap(r -> r, this::process));

        LOGGER.info("Number of results: {}", results.size());

        writeData("step4.json", results);

        LOGGER.info("DONE");
    }

    private List<URL> process(final String url) {
        final Document doc = getDocument(url);

        if (doc == null) {
            LOGGER.info("can not fetch '{}'", url);
            return Collections.emptyList();
        }

        final Elements elements = doc.select("span.mb-3 > span:nth-child(1) > a");

        LOGGER.debug("found {} elements for {}", elements.size(), url);

        final List<URL> references = elements.stream()
                .filter(e -> e.tag().getName().equals("a"))
                .map(Element::attributes)
                .map(a -> a.get("href"))
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(this::getUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return references;
    }

    private Document getDocument(final String url) {
        Document result = null;
        int retryCount = 0;

        do {
            retryCount++;
            try {
                LOGGER.debug("Query: '{}'", url);
                result = Jsoup.connect(url).get();
                sleepRateLimit();
            } catch (HttpStatusException e) {
                if (e.getStatusCode() == 429) {
                    LOGGER.info("Rate Limit Hit - sleeping");
                    sleep429();
                } else {
                    LOGGER.info("Got respone {}", e.getStatusCode());
                }
            } catch (IOException e) {
                LOGGER.warn("Something went wrong for URL '{}'", url, e);
            }
        } while (result == null && retryCount <= 5);

        return result;
    }

    private URL getUrl(final String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            LOGGER.warn("can't create URL from String '{}'", url);
        }

        return null;
    }

}
