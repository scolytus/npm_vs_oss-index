package io.github.scolytus.npmvsoss.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllData {

    private List<Integer> skipped = new ArrayList<>();

    private List<Finding> allFindings = new ArrayList<>();

    private Map<String, Counter> errors = new HashMap<>();

    private List<Integer> retry = new ArrayList<>();

    private List<Integer> error404 = new ArrayList<>();

    private List<Integer> error429 = new ArrayList<>();

    private List<Integer> error5xx = new ArrayList<>();

    private Map<String, PackageVersion> packageVersions = new HashMap<>();

    public List<Integer> getSkipped() {
        return skipped;
    }

    public void setSkipped(List<Integer> skipped) {
        this.skipped = skipped;
    }

    public List<Finding> getAllFindings() {
        return allFindings;
    }

    public void setAllFindings(List<Finding> allFindings) {
        this.allFindings = allFindings;
    }

    public Map<String, Counter> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, Counter> errors) {
        this.errors = errors;
    }

    public List<Integer> getRetry() {
        return retry;
    }

    public void setRetry(List<Integer> retry) {
        this.retry = retry;
    }

    public Map<String, PackageVersion> getPackageVersions() {
        return packageVersions;
    }

    public void setPackageVersions(Map<String, PackageVersion> packageVersions) {
        this.packageVersions = packageVersions;
    }

    public List<Integer> getError404() {
        return error404;
    }

    public void setError404(List<Integer> error404) {
        this.error404 = error404;
    }

    public List<Integer> getError429() {
        return error429;
    }

    public void setError429(List<Integer> error429) {
        this.error429 = error429;
    }

    public List<Integer> getError5xx() {
        return error5xx;
    }

    public void setError5xx(List<Integer> error5xx) {
        this.error5xx = error5xx;
    }
}
