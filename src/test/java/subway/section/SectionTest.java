package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.exceptions.InvalidSectionException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {

    @DisplayName("가능한 거리값인가")
    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0})
    void nonemptyArgument(int distance) {
        assertThatThrownBy(() -> {
            new Section(4, 6, distance);
        }).isInstanceOf(InvalidSectionException.class);
    }
}
