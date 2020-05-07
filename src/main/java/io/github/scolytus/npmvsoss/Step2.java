package io.github.scolytus.npmvsoss;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.scolytus.npmvsoss.data.AllData;
import io.github.scolytus.npmvsoss.data.OSSIndexInput;
import io.github.scolytus.npmvsoss.data.OSSIndexResponse;
import io.github.scolytus.npmvsoss.data.PackageVersion;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Step2 extends AbstractStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(Step2.class);

    private static final String API_BASE_URL = "https://ossindex.sonatype.org/api/v3/component-report";

    private static final UnirestInstance UNIREST_INSTANCE = initUniRest();

    private AllData allData = null;

    private String apiToken = getApiToken();
    private String apiUsername = getApiUsername();

    public Step2() {
        initAllData();
    }

    public void run() {
        final Map<String, PackageVersion> packageVersions = allData.getPackageVersions();
        final List<String> all = new ArrayList<>(packageVersions.keySet());
        all.sort(String::compareTo);

        final int batchSize = 120;
        int start = 0;
        int end;
        int batchCount = 1;

        OSSIndexResponse allResponses = new OSSIndexResponse();

        do {
            end = start + batchSize;
            if (end > all.size()) {
                end = all.size();
            }

            LOGGER.info("Processing batch '{}' from [{}] to [{}] of {} ({}%)", batchCount, start, end - 1, all.size(), ((end - 1)*100.0/(all.size() * 100.0)));

            final List<String> batch = all.subList(start, end);

            final OSSIndexInput input = new OSSIndexInput();
            input.setCoordinates(convert(batch));

            final OSSIndexResponse response = submit(input);

            allResponses.addAll(response);

            response.forEach(r -> {
                final String version = getVersionFromPurl(r.getCoordinates());
                packageVersions.get(version).setOssIndexReport(r);
            });

            sleepRateLimit();

            start = end;
            batchCount++;
        } while (end < all.size());

        LOGGER.info("here");

        writeResponse(allResponses);
        writeAllData();

        LOGGER.info("DONE");

    }

    // https://github.com/DependencyTrack/dependency-track/blob/master/src/main/java/org/dependencytrack/tasks/scanners/OssIndexAnalysisTask.java
    private OSSIndexResponse submit(final OSSIndexInput payload) throws UnirestException {
        HttpResponse<OSSIndexResponse> response;
        do {
            response = submitIntern(payload);
            if (response.getStatus() == 429) {
                sleep429();
            }
        } while (response.getStatus() == 429);

        return response.getBody();
    }

    private HttpResponse<OSSIndexResponse> submitIntern(final OSSIndexInput payload) throws UnirestException {
        final HttpResponse<OSSIndexResponse> ossIndexResponseHttpResponse = UNIREST_INSTANCE.post(API_BASE_URL)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.USER_AGENT, getUserAgent())
                .basicAuth(apiUsername, apiToken)
                .body(payload)
                .asObject(OSSIndexResponse.class);

        return ossIndexResponseHttpResponse;
    }

    private void initAllData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            allData = mapper.readValue(Paths.get("step1.json").toFile(), AllData.class);
        } catch (IOException e) {
            LOGGER.error("Can't read Data", e);
            throw new IllegalStateException("Can't read Data", e);
        }
    }

    private void writeResponse(OSSIndexResponse response) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("step2.json").toFile(), response);
        } catch (IOException e) {
            LOGGER.info("Something went wrong");
        }
    }

    private void writeAllData() {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("step2.allData.json").toFile(), allData);
        } catch (IOException e) {
            LOGGER.info("Something went wrong");
        }
    }

    private List<String> convert(final List<String> batch) {
        return batch.stream()
                .map(this::getPurl)
                .collect(Collectors.toList());
    }

    private String getPurl(final PackageVersion packageVersion) {
        return getPurl(packageVersion.toString());
    }

    private String getPurl(final String packageVersion) {
        String sToUse = packageVersion;
        if (packageVersion.startsWith("@")) {
            sToUse = "%40" + packageVersion.substring(1);
        }
        return "pkg:npm/" + sToUse;
    }

    private String getUserAgent() {
        return "io.github.scolytus.npm_vs_oss-index";
    }

    private String getVersionFromPurl(final String purl) {
        final String withoutPrefix = purl.substring(8);
        if (withoutPrefix.startsWith("%40")) {
            return "@" + withoutPrefix.substring(3);
        }

        return withoutPrefix;
    }

    private String getApiUsername() {
        return System.getProperty("io.github.scolytus.user");
    }

    private String getApiToken() {
        return System.getProperty("io.github.scolytus.token");
    }

    private static UnirestInstance initUniRest() {
        final UnirestInstance unirestInstance = Unirest.primaryInstance();
        unirestInstance.config().setObjectMapper(new JacksonObjectMapper());
        return unirestInstance;
    }
}
