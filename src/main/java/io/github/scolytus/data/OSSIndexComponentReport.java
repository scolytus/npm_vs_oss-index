package io.github.scolytus.data;

import java.util.List;

public class OSSIndexComponentReport {

    private String description;
    private String coordinates;
    private String reference;

    private List<OSSIndexComponentReportVulnerability> vulnerabilities;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<OSSIndexComponentReportVulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(List<OSSIndexComponentReportVulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }
}
