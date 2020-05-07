package io.github.scolytus.npmvsoss;

import com.google.common.base.Stopwatch;
import io.github.scolytus.npmvsoss.data.Finding;
import io.github.scolytus.npmvsoss.data.MissingNPMAdvisory;
import io.github.scolytus.npmvsoss.data.PackageVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Step3 extends AbstractStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(Step3.class);

    public Step3() {
        initAllData("step2.allData.json");
    }

    @Override
    public void run() {
        //playWithData();
        //playWithData2();

        countAdvisories();
        countPackages();

        case2();
    }

    private void case2() {
        Map<Integer, List<Finding>> findings = new HashMap<>();

        // per npm advisory - get all found advisories
        // collect all affected findings for any advisory
        allData.getAllFindings().forEach(f -> {
            if (f.affected) {
                final List<Finding> list = findings.computeIfAbsent(f.advisory, k -> new ArrayList<>());
                list.add(f);
            }
        });

        List<MissingNPMAdvisory> missing = new ArrayList<>();

        // now check how many of these findings have a report from OSS Index
        findings.forEach((k,v) -> {
            LOGGER.debug("Processing {}", k);
            int ossReportCount = 0;
            for (Finding finding : v) {
                final PackageVersion packageVersion = allData.getPackageVersions().get(PackageVersion.toString(finding));
                ossReportCount += packageVersion.getOssIndexReport().getVulnerabilities().size();
            }
            if (ossReportCount == 0) {
                // This advisory is for sure missing in OSS Index
                LOGGER.debug("Advisory {} has {} affected versions but {} OSS reports",
                        k, v.size(), ossReportCount);
                missing.add(new MissingNPMAdvisory(v.get(0)));
            }
        });

        LOGGER.info("Found {} npm advisories missing in OSS", missing.size());

        writeData("step3.missing.json", missing);
    }

    private void countAdvisories() {
        Set<Integer> foundAdvisories = new HashSet<>();
        allData.getPackageVersions().forEach((k, v) -> {
            v.getFindings().forEach(f -> foundAdvisories.add(f.advisory));
        });

        final Set<Integer> collect = allData.getAllFindings().stream().map(f -> f.advisory).collect(Collectors.toSet());

        LOGGER.info("Found {} advisories (control: {})", foundAdvisories.size(), collect.size());
    }

    private void countPackages() {
        final Set<String> collect = allData.getAllFindings().stream().filter(f -> f.affected).map(f -> f.packageName).collect(Collectors.toSet());

        LOGGER.info("Found {} unique packages", collect.size());
    }

    private void playWithData2() {

        final Stopwatch stopwatch = Stopwatch.createStarted();

        final Set<String> packageVersions = allData.getPackageVersions().keySet();
        final List<String> all = new ArrayList<>(packageVersions);
        all.sort(String::compareTo);

        int discrepancy = 0;
        int ossFalseNegative = 0;

        List<String> ossFalseNegatives = new ArrayList<>();

        for (String packageVersion : all) {
            final PackageVersion data = allData.getPackageVersions().get(packageVersion);
            final long npmSize = data.getFindings().stream().filter(f -> f.affected).count();
            final int ossSize = data.getOssIndexReport().getVulnerabilities().size();
            if (npmSize != ossSize) {
                LOGGER.debug("Discrepancy: {}: {} vs {} {}",
                        packageVersion,
                        npmSize,
                        ossSize,
                        data.getOssIndexReport().getReference());
                discrepancy++;
                if (ossSize == 0) {
                    ossFalseNegative++;
                    ossFalseNegatives.add(packageVersion);
                }
            }
        }

        LOGGER.info("Processed {} elements in {}ms found {} discrepancies, {} OSS Index false negatives",
                packageVersions.size(),
                stopwatch.elapsed(TimeUnit.MILLISECONDS),
                discrepancy,
                ossFalseNegative);

        for (String falseNegative : ossFalseNegatives) {
            final PackageVersion current = allData.getPackageVersions().get(falseNegative);
            LOGGER.info("False Negative: {} - {}",
                    falseNegative,
                    current.getOssIndexReport().getReference());
        }
    }

    private void playWithData() {

        final Stopwatch stopwatch = Stopwatch.createStarted();

        final Set<String> packageVersions = allData.getPackageVersions().keySet();
        final List<String> all = new ArrayList<>(packageVersions);
        all.sort(String::compareTo);

        int discrepancy = 0;
        int ossFalseNegative = 0;

        List<String> ossFalseNegatives = new ArrayList<>();

        for (String packageVersion : all) {
            final PackageVersion data = allData.getPackageVersions().get(packageVersion);
            final int npmSize = data.getFindings().size();
            final int ossSize = data.getOssIndexReport().getVulnerabilities().size();
            if (npmSize != ossSize) {
                LOGGER.debug("Discrepancy: {}: {} vs {} {}",
                        packageVersion,
                        npmSize,
                        ossSize,
                        data.getOssIndexReport().getReference());
                discrepancy++;
                if (ossSize < npmSize) {
                    ossFalseNegative++;
                    ossFalseNegatives.add(packageVersion);
                }
            }
        }

        LOGGER.info("Processed {} elements in {}ms found {} discrepancies, {} OSS Index false negatives",
                packageVersions.size(),
                stopwatch.elapsed(TimeUnit.MILLISECONDS),
                discrepancy,
                ossFalseNegative);

//        for (String falseNegative : ossFalseNegatives) {
//            LOGGER.info("False Negative: {} - {}",
//                    falseNegative,
//                    allData.getPackageVersions().get(falseNegative).getOssIndexReport().getReference());
//        }
    }
}
