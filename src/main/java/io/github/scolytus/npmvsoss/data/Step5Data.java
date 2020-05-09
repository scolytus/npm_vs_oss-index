package io.github.scolytus.npmvsoss.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.InternetDomainName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Step5Data {

    private static final Logger LOGGER = LoggerFactory.getLogger(Step5Data.class);

    private final Map<String, List<Relation>> oss2ref = new HashMap<>();

    private final Map<String, List<Relation>> ref2oss = new HashMap<>();

    private final Map<String, List<Relation>> host2oss = new HashMap<>();

    private final Map<String, List<Integer>> oss2npm = new HashMap<>();

    public void add(final String ossVuln, final URL reference) {
        final Relation relation = new Relation(ossVuln, reference);

        oss2ref.computeIfAbsent(ossVuln, ov -> new ArrayList<>())
                .add(relation);

        ref2oss.computeIfAbsent(reference.toString(), ref -> new ArrayList<>())
                .add(relation);

        host2oss.computeIfAbsent(relation.getRefDomain(), h -> new ArrayList<>())
                .add(relation);

        if (relation.isNpmAdvisory()) {
            oss2npm.computeIfAbsent(ossVuln, ov -> new ArrayList<>())
                    .add(relation.getNpmAdvisory());
        }
    }

    public Map<String, List<Relation>> getOss2ref() {
        return oss2ref;
    }

    public Map<String, List<Relation>> getRef2oss() {
        return ref2oss;
    }

    public Map<String, List<Relation>> getHost2oss() {
        return host2oss;
    }

    public Map<String, List<Integer>> getOss2npm() {
        return oss2npm;
    }

    public static class Relation {

        private final String ossVuln;

        private final URL reference;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public Relation(@JsonProperty("ossVuln") String ossVuln, @JsonProperty("reference") URL reference) {
            this.ossVuln = ossVuln;
            this.reference = reference;
        }

        @JsonIgnore
        public String getRefHost() {
            return reference.getHost();
        }

        @JsonIgnore
        public String getRefDomain() {
            return InternetDomainName.from(getRefHost()).topPrivateDomain().toString();
        }

        @JsonIgnore
        public boolean isNpmAdvisory() {
            return Step5DataUtil.isNpmAdvisory(this);
        }

        public Integer getNpmAdvisory() {
            final int advisory = Step5DataUtil.getNpmAdvisory(this);
            return advisory >= 0 ? advisory : null;
        }

        public String getOssVuln() {
            return ossVuln;
        }

        public URL getReference() {
            return reference;
        }
    }
}
