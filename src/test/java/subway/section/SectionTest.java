package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.exceptions.InvalidValueException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {

    Section section;

    @BeforeEach
    void setUp() {
        section = new Section(10, 30, 100);
    }

    @DisplayName("가능한 거리값인가")
    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0})
    void nonemptyArgument(int distance) {
        assertThatThrownBy(() -> {
            new Section(4, 6, distance);
        }).isInstanceOf(InvalidValueException.class);
    }

    @DisplayName("상행역 및 거리 변경")
    @Test
    void updateUpStationAndDistance() {
        Section expectedSection = new Section(15, 30, 50);

        section.updateUpStationAndDistance(15, 50);

        assertThat(section).isEqualTo(expectedSection);
    }

    @DisplayName("하행역 및 거리 변경")
    @Test
    void updateDownStationAndDistance() {
        Section expectedSection = new Section(10, 33, 75);

        section.updateDownStationAndDistance(33, 25);

        assertThat(section).isEqualTo(expectedSection);
    }
}
