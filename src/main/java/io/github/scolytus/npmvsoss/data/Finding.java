package io.github.scolytus.npmvsoss.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Finding {

    public final boolean affected;
    public final int advisory;
    public final String packageName;
    public final String version;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Finding(@JsonProperty("packageName") String packageName, @JsonProperty("version") String version,
                   @JsonProperty("advisory") int advisory, @JsonProperty("affected") boolean affected) {
        this.affected = affected;
        this.advisory = advisory;
        this.packageName = packageName;
        this.version = version;
    }

    @Override
    public String toString() {
        return "{" + packageName + "@" + version + " (" + advisory + " -> " + affected + ")}";
    }
}
