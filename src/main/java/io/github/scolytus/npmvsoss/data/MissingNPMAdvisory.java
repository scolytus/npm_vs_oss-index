package io.github.scolytus.npmvsoss.data;

import io.github.scolytus.npmvsoss.Step1;
import io.github.scolytus.npmvsoss.Step2;

public class MissingNPMAdvisory {

    private int advisory;
    private String packageName;

    public MissingNPMAdvisory() {
    }

    public MissingNPMAdvisory(final Finding finding) {
        advisory = finding.advisory;
        packageName = finding.packageName;
    }

    public int getNpmAdvisory() {
        return advisory;
    }

    public String getNpmAdvisoryURL() {
        return Step1.BASE_URL + advisory + "/";
    }

    public String getOSSIndexUrl() {
        return "https://ossindex.sonatype.org/component/" + Step2.getPurl(packageName);
    }

}
