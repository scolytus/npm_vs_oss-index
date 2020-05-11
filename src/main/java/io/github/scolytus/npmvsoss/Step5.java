package io.github.scolytus.npmvsoss;

import io.github.scolytus.npmvsoss.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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

        step5Data.getOss2npm().forEach((k, v) -> {
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

        final Map<String, OSSFalsePositive> ossFalsePositives = new HashMap<>();

        packageVersions.forEach((packageATversion, packageVersionObject) -> {
            final OSSIndexComponentReport ossIndexReport = packageVersionObject.getOssIndexReport();
            final List<OSSIndexComponentReportVulnerability> vulnerabilities = ossIndexReport.getVulnerabilities();
            if (!vulnerabilities.isEmpty()) {
                vulnerabilities.forEach(vulnerabilityMap -> {
                    final String reference = (String) vulnerabilityMap.get("reference");
                    final List<Integer> npmAdvisories = step5Data.getOss2npm().get(reference);
                    final List<Step5Data.Relation> references = step5Data.getOss2ref().get(reference);
                    if (npmAdvisories != null && !npmAdvisories.isEmpty()) {
                        npmAdvisories.stream().filter(Objects::nonNull).forEach(i -> {
                            final List<Finding> findings = packageVersionObject.getFindings();
                            findings.forEach(f -> {
                                if (i == f.advisory) {
                                    final String key = reference + "@@@" + f.advisory;
                                    final OSSFalsePositive ossFalsePositive = ossFalsePositives.computeIfAbsent(key,
                                            k -> new OSSFalsePositive(reference, Step1.getVersionUrl(f.advisory)));
                                    if (f.affected) {
                                        LOGGER.info("OK:  OSS {} vs NPM {} for {} - {}", i, f.advisory, packageATversion, f.affected);
                                        oks.add(packageVersionObject);
                                    } else {
                                        LOGGER.info("NOK: OSS {} vs NPM {} for {} - {}", i, f.advisory, packageATversion, f.affected);
                                        falsePositives.add(packageVersionObject);
                                        ossFalsePositive.add(packageATversion, ossIndexReport.getReference());
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

        final List<OSSFalsePositive> resultingFalsePositives = ossFalsePositives.values().stream()
                .filter(fp -> !fp.getFalsePositives().isEmpty())
                .sorted(Comparator.comparing(OSSFalsePositive::getNpmUrl))
                .collect(Collectors.toList());

        writeData("step5.resultingFalsePositives.json", resultingFalsePositives);

        LOGGER.info("OSS Reports with false positives: {} out of {} with npm source",
                resultingFalsePositives.size(), step5Data.getOss2npm().size());

        final OSSFalsePositive mostFalsePositives = resultingFalsePositives.stream()
                .max(Comparator.comparingInt(fp -> fp.getFalsePositives().size()))
                .get();

        LOGGER.info("Report '{}' has {} false positives",
                mostFalsePositives.getOssUrl(), mostFalsePositives.getFalsePositives().size());

        LOGGER.info("OSS Reports: {}", step5Data.getOss2ref().size());

        LOGGER.info("Unique name@version instances with false positives: {}",
                falsePositives.stream().map(PackageVersion::toString).collect(Collectors.toSet()).size());

        LOGGER.info("DONE");
    }
}
