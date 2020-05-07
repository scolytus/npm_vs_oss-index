package io.github.scolytus.npmvsoss;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testVersionUrl() {
        assertThat(Step1.getVersionUrl(1), is("https://www.npmjs.com/advisories/1/versions"));
        assertThat(Step1.getVersionUrl(10), is("https://www.npmjs.com/advisories/10/versions"));
        assertThat(Step1.getVersionUrl(1500), is("https://www.npmjs.com/advisories/1500/versions"));
        assertThat(Step1.getVersionUrl(2000), is("https://www.npmjs.com/advisories/2000/versions"));
    }

}
