package io.github.scolytus;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
        assertThat(App.getVersionUrl(1), is("https://www.npmjs.com/advisories/1/versions"));
        assertThat(App.getVersionUrl(10), is("https://www.npmjs.com/advisories/10/versions"));
        assertThat(App.getVersionUrl(1500), is("https://www.npmjs.com/advisories/1500/versions"));
        assertThat(App.getVersionUrl(2000), is("https://www.npmjs.com/advisories/2000/versions"));
    }
}
