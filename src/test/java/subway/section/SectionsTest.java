package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static subway.section.Section.TERMINAL_ID;

class SectionsTest {

    @DisplayName("구간을 이루는 갯수가 3개 미만일 경우 예외가 발생한다")
    @Test
    void createFail1() {
        // given
        List<Section> sections = Arrays.asList(
                new Section(1L, TERMINAL_ID, 1L, 3),
                new Section(1L, 1L, TERMINAL_ID, 3)
        );

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> new Sections(sections))
                .withMessage("구간은 최소 3개로 이루어져야 합니다.");
    }

    @DisplayName("상/하행 종점 중 하나라도 포함되어 있지 않을 경우 예외가 발생한다")
    @Test
    void createFail2() {
        // given
        List<Section> sections = Arrays.asList(
                new Section(1L, 2L, 1L, 3),
                new Section(1L, TERMINAL_ID, 2L, 3),
                new Section(1L, 1L, 3L, 3)
        );

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> new Sections(sections))
                .withMessage("상/하행 종점이 모두 포함되어야 합니다.");
    }

    /**
     * TERMINAL, 2, 3, 4, 1, 5, TERMINAL
     */
    @DisplayName("정렬된 구간의 역 ID를 모두 반환한다")
    @Test
    void getStationIds() {
        // given
        List<Section> given = Arrays.asList(
                new Section(1L, 3L, 4L, 3),
                new Section(1L, 2L, 3L, 3),
                new Section(1L, 1L, 5L, 3),
                new Section(1L, 4L, 1L, 3),
                new Section(1L, 5L, TERMINAL_ID, 3),
                new Section(1L, TERMINAL_ID, 2L, 3)
        );
        Sections sections = new Sections(given);

        // when
        List<Long> result = sections.getSortedStationIds();

        // then
        assertThat(result).isEqualTo(Arrays.asList(2L, 3L, 4L, 1L, 5L));
    }

    @DisplayName("주어진 구간과 상/하행역 중 같은 역이 있는 구간을 찾는다")
    @ParameterizedTest
    @CsvSource({"2,4,0", "3,1,0", "5,2,1", "1,6,2"})
    void findBySameStation(long upStationId, long downStationId, int expectedIdx) {
        // given
        List<Section> given = Arrays.asList(
                new Section(1L, 2L, 1L, 3),
                new Section(1L, TERMINAL_ID, 2L, 3),
                new Section(1L, 1L, 3L, 3),
                new Section(1L, 3L, TERMINAL_ID, 3)
        );
        Sections sections = new Sections(given);

        // when
        Section result = sections.findBySameUpOrDownStationWith(new Section(1L, upStationId, downStationId, 4));

        // then
        assertThat(result).isEqualTo(given.get(expectedIdx));
    }
}
