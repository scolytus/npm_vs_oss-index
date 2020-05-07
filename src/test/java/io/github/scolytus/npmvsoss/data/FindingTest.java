package io.github.scolytus.npmvsoss.data;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FindingTest {

    @Test
    public void testToString() {
        final String s = new Finding("foo", "bar", 1234, false).toString();

        assertThat(s, is("{foo@bar (1234 -> false)}"));
    }
}
