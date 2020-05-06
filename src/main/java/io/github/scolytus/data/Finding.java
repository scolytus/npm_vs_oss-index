package io.github.scolytus.data;

public class Finding {

    public final boolean affected;
    public final  int advisory;
    public final  String packageName;
    public final  String version;

    public Finding(String packageName, String version, int advisory, boolean affected) {
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
