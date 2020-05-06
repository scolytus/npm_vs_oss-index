package io.github.scolytus.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PackageVersion {

    private String packageName;
    private String version;
    private List<Finding> findings = new ArrayList<>();

    public PackageVersion(String packageName, String version) {
        this.packageName = packageName;
        this.version = version;
    }

    public synchronized void add(Finding finding) {
        findings.add(finding);
    }

    public List<Finding> getFindings() {
        return findings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageVersion that = (PackageVersion) o;
        return Objects.equals(packageName, that.packageName) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, version);
    }

    @Override
    public String toString() {
        return toString(packageName, version);
    }

    public static String toString(final Finding finding) {
        return toString(finding.packageName, finding.version);
    }

    public static String toString(final String packageName, final String version) {
        return "" + packageName + "@" + version;
    }
}
