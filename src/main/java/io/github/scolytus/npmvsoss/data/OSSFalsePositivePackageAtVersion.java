package io.github.scolytus.npmvsoss.data;

public class OSSFalsePositivePackageAtVersion {

    private String packageAtName;
    private String ossComponentUrl;

    public OSSFalsePositivePackageAtVersion() {
    }

    public OSSFalsePositivePackageAtVersion(final String packageAtName, final String ossComponentUrl) {
        this.packageAtName = packageAtName;
        this.ossComponentUrl = ossComponentUrl;
    }

    public String getPackageAtName() {
        return packageAtName;
    }

    public void setPackageAtName(String packageAtName) {
        this.packageAtName = packageAtName;
    }

    public String getOssComponentUrl() {
        return ossComponentUrl;
    }

    public void setOssComponentUrl(String ossComponentUrl) {
        this.ossComponentUrl = ossComponentUrl;
    }
}
