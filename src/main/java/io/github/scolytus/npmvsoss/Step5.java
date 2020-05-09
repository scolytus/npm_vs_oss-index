package io.github.scolytus.npmvsoss;

import io.github.scolytus.npmvsoss.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Step5 extends AbstractStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(Step5.class);

    private Step4Result step4;

    public Step5() {
        initAllData("step2.allData.json");
        step4 = load("step4.json", Step4Result.class);
    }

    @Override
    public void run() {
        LOGGER.info("Loaded {} step4 results", step4.size());

        final Step5Data step5Data = new Step5Data();

        step4.forEach((key, value) -> value.forEach(v -> step5Data.add(key, v)));

        step5Data.getOss2npm().forEach((k,v) -> {
            if (v.size() > 1) {
                LOGGER.warn("found strange data: '{}' --> {}", k, v);
            }
        });

        LOGGER.info("sorted things...");

        writeData("step5.json", step5Data);

        LOGGER.info("Data written");

        final Map<String, PackageVersion> packageVersions = allData.getPackageVersions();

        List<PackageVersion> falsePositives = new ArrayList<>();
        List<PackageVersion> oks = new ArrayList<>();

        packageVersions.forEach((packageATversion, packageVersionObject) -> {
            final OSSIndexComponentReport ossIndexReport = packageVersionObject.getOssIndexReport();
            final List<OSSIndexComponentReportVulnerability> vulnerabilities = ossIndexReport.getVulnerabilities();
            if (!vulnerabilities.isEmpty()) {
                vulnerabilities.forEach(vulnerabilityMap -> {
                    final Object reference = vulnerabilityMap.get("reference");
                    final List<Integer> npmAdvisories = step5Data.getOss2npm().get(reference);
                    final List<Step5Data.Relation> references = step5Data.getOss2ref().get(reference);
                    if (npmAdvisories != null && !npmAdvisories.isEmpty()) {
                        npmAdvisories.stream().filter(Objects::nonNull).forEach(i -> {
                            final List<Finding> findings = packageVersionObject.getFindings();
                            findings.forEach(f -> {
                                if (i == f.advisory) {
                                    if (f.affected) {
                                        LOGGER.info("OK:  OSS {} vs NPM {} for {} - {}", i, f.advisory, packageATversion, f.affected);
                                        oks.add(packageVersionObject);
                                    } else {
                                        LOGGER.info("NOK: OSS {} vs NPM {} for {} - {}", i, f.advisory, packageATversion, f.affected);
                                        falsePositives.add(packageVersionObject);
                                    }
                                }
                            });
                        });
                    } else {
                        // other source than npm audit
                        // LOGGER.info("No OSS data for {} | {}", packageATversion, reference);
                    }
                });
            }
        });

        LOGGER.info("DONE");
    }
}
