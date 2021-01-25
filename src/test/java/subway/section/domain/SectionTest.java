package subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("두 역간의 거리의 차를 반환한다")
    @Test
    void testDistanceDiff() {
        assertThat(base.distanceDiff(other)).isEqualTo(Math.abs(BASE_DISTANCE - OTHER_DISTANCE));
    }

    @DisplayName("상대 역이 구간 상에서 자신보다 늦은 역인지 확인한다")
    @Test
    void testIsUpperThan() {
        assertThat(base.isUpperThan(other)).isFalse();
        assertThat(other.isUpperThan(base)).isTrue();
    }

    @DisplayName("받은 두 역 중에서 처음 받은 역이 다음 받은 역보다 가까운지 확인한다")
    @Test
    void testIsCloserFromThan() {
        Section thanSection = new Section(1, 1, 3);

        assertThat(base.isFartherOrEqualFromThan(other, thanSection)).isTrue();
        assertThat(base.isFartherOrEqualFromThan(thanSection, other)).isFalse();
    }
}
