package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {
    private final int BASE_DISTANCE = 5;
    private final int OTHER_DISTANCE = 1;
    private Section base;
    private Section other;

    @BeforeEach
    void setUp() {
        base = new Section(1, 1, BASE_DISTANCE);
        other = new Section(1, 1, OTHER_DISTANCE);
    }

    @Test
    void testDistanceDiff() {
        assertThat(base.distanceDiff(other)).isEqualTo(Math.abs(BASE_DISTANCE - OTHER_DISTANCE));
    }

    @Test
    void testIsUpperThan() {
        assertThat(base.isUpperThan(other)).isFalse();
        assertThat(other.isUpperThan(base)).isTrue();
    }

    @Test
    void testIsCloserFromThan() {
        Section thanSection = new Section(1, 1, 3);

        assertThat(base.isCloserFromThan(other, thanSection)).isFalse();
        assertThat(base.isCloserFromThan(thanSection, other)).isTrue();
    }
}
