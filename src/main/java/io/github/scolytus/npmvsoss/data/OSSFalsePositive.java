package io.github.scolytus.npmvsoss.data;

import java.util.ArrayList;
import java.util.List;

public class OSSFalsePositive {

    private String ossUrl;

    private String npmUrl;

    private List<OSSFalsePositivePackageAtVersion> falsePositives = new ArrayList<>();

    public OSSFalsePositive() {
    }

    public OSSFalsePositive(final String ossUrl, final String npmUrl) {
        this.ossUrl = ossUrl;
        this.npmUrl = npmUrl;
    }

    public void add(final String packageAtName, final String ossComponentUrl) {
        falsePositives.add(new OSSFalsePositivePackageAtVersion(packageAtName, ossComponentUrl));
    }

    public String getOssUrl() {
        return ossUrl;
    }

    public void setOssUrl(String ossUrl) {
        this.ossUrl = ossUrl;
    }

    public String getNpmUrl() {
        return npmUrl;
    }

    public void setNpmUrl(String npmUrl) {
        this.npmUrl = npmUrl;
    }

    public List<OSSFalsePositivePackageAtVersion> getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(List<OSSFalsePositivePackageAtVersion> falsePositives) {
        this.falsePositives = falsePositives;
    }
}
