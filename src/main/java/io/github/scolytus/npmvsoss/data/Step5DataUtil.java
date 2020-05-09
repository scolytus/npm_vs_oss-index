package io.github.scolytus.npmvsoss.data;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Step5DataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(Step5DataUtil.class);

    public static boolean isNpmAdvisory(final Step5Data.Relation relation) {
        return isOld(relation) || isNew(relation);
    }

    public static int getNpmAdvisory(final Step5Data.Relation relation) {
        if (isNpmAdvisory(relation)) {
            return extractInt(relation);
        }

        return -1;
    }

    private static boolean isNew(final Step5Data.Relation relation) {
        if ("npmjs.com".equals(relation.getRefDomain())) {
            return relation.getReference().getPath().startsWith("/advisories");
        }

        return false;
    }

    private static boolean isOld(final Step5Data.Relation relation) {
        if ("nodesecurity.io".equals(relation.getRefDomain())) {
            if (relation.getReference().getPath().startsWith("/advisories")) {
                return extractInt(relation) >= 0;
            }
        }

        return false;
    }

    private static int extractInt(final Step5Data.Relation relation) {
        try {
            return Integer.parseInt(StringUtils.substringAfter(relation.getReference().getPath(), "/advisories/"));
        } catch (Exception e) {
            return -1;
        }
    }

}
