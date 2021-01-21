package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import subway.line.Line;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    public static Stream<Arguments> SectionParamGenerator() {
        return Stream.of(
                Arguments.of(0L, 0L, Line.HEADID, 1L, 5, false),
                Arguments.of(0L, 0L, 3L, Line.TAILID, 5, false),
                Arguments.of(0L, 0L, 2L, 4L, 2, true),
                Arguments.of(0L, 0L, 1L, 3L, 5, false)
        );
    }

    @Test
    @DisplayName("Section upStation 체크 메서드 테스트")
    public void sectionTest() {
        Section section = new Section(1L, 1L, 2L, 3L, 5);
        assertThat(section.isUpStation(2L)).isTrue();
        assertThat(section.isUpStation(1L)).isFalse();
    }

    @Test
    @DisplayName("Section downStation 체크 메서드 테스트")
    public void sectionTest1() {
        Section section = new Section(1L, 1L, 2L, 3L, 5);
        assertThat(section.isDownStation(3L)).isTrue();
        assertThat(section.isDownStation(4L)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("SectionParamGenerator")
    @DisplayName("Section isExist 메서드 테스트")
    public void isExist(Long id, Long lineId, Long upStationId, Long downStationId, int distance, boolean answer) {
        // given
        Section section = new Section(id, lineId, upStationId, downStationId, distance);

        // when
        Section newSection = new Section(1L, lineId, 2L, 3L, 3);

        // then
        assertThat(section.isExist(newSection)).isEqualTo(answer);
    }

}
