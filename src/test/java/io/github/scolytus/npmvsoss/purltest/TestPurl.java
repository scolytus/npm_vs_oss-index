package io.github.scolytus.npmvsoss.purltest;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.PackageURLBuilder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestPurl {

    @Test
    public void test1() throws MalformedPackageURLException {
        final PackageURL impalaBmap = PackageURLBuilder.aPackageURL()
                .withName("bmap")
                .withType("npm")
                .withNamespace("@impala")
                .build();

        assertThat(impalaBmap.toString(), is("pkg:npm/%40impala/bmap"));
    }

    @Test
    public void test2() throws MalformedPackageURLException {
        final PackageURL purlFromSpec = new PackageURL("pkg:npm/%40angular/animation@12.3.1");
        assertThat(purlFromSpec.getVersion(), is("12.3.1"));
        assertThat(purlFromSpec.getType(), is("npm"));
        assertThat(purlFromSpec.getName(), is("animation"));
        assertThat(purlFromSpec.getNamespace(), is("@angular"));
    }

}
