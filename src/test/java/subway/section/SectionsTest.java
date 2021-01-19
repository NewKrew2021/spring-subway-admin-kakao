package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class SectionsTest {

    @DisplayName("구간들이 주어지면 구간 일급 컬렉션이 생성된다")
    @Test
    void create() {
        // given
        List<Section> sections = Arrays.asList(
                new Section(1L, 1L, 0),
                new Section(1L, 3L, 1),
                new Section(1L, 4L, 2),
                new Section(1L, 5L, 3)
        );

        // when
        Sections result = Sections.from(sections);

        // then
        assertThat(result).extracting("sections")
                .usingRecursiveComparison()
                .isEqualTo(sections);
    }

    @DisplayName("구간의 갯수가 2개보다 적으면 생성에 실패한다")
    @Test
    void createFail() {
        // given
        List<Section> sections = Collections.singletonList(new Section(1L, 1L, 0));

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> Sections.from(sections))
                .withMessage("구간에 포함되는 역의 갯수는 두개 이상이어야 합니다");
    }

    /**
     * 10은 새로 추가될 구간의 역 id
     * <p>
     * 위치:     -2 -1 0 1 2 3 4 5 6 7 8 9
     * 기존:           1         2
     * 추가 위치:  x        x x       x
     */
    @DisplayName("상/하행역과 거리가 주어지면 새로 추가할 구간을 생성한다")
    @ParameterizedTest
    @CsvSource({"1,10,2,2", "10,1,2,-2", "10,2,2,3", "2,10,2,7"})
    void createNewSection(long upStationId, long downStationId, int distance, int expectedPosition) {
        // given
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, 1L, 1L, 0),
                new Section(2L, 1L, 2L, 5)
        ));
        // when
        Section newSection = sections.createNewSection(upStationId, downStationId, distance);

        // then
        assertThat(newSection).usingRecursiveComparison()
                .isEqualTo(new Section(1L, 10, expectedPosition));
    }

    @DisplayName("새로운 구간 생성시, 상/하행역이 모두 기존 구간에 포함되어 있거나 모두 포함되어 있지 않다면 예외가 발생한다")
    @ParameterizedTest
    @CsvSource({"1,2", "100,101"})
    void createNewSectionFail1(long upStationId, long downStationId) {
        // given
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, 1L, 1L, 0),
                new Section(2L, 1L, 2L, 5)
        ));

        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> sections.createNewSection(upStationId, downStationId, 2))
                .withMessage("상/하행역 중 하나만 일치해야합니다");
    }

    @DisplayName("새로운 구간 생성시, 추가할 구간의 거리가 기존 거리보다 크거나 같으면 예외가 발생한다")
    @ParameterizedTest
    @CsvSource({"1,10,5", "1,10,100", "10,2,5", "10,2,100"})
    void createNewSectionFail2(long upStationId, long downStationId, int distance) {
        // given
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, 1L, 1L, 0),

                new Section(2L, 1L, 2L, 5)
        ));
        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> sections.createNewSection(upStationId, downStationId, distance))
                .withMessage("기존 구간보다 새로 생긴 구간의 거리가 더 짧아야합니다");
    }

    @DisplayName("역 id가 주어지면 구간 일급 컬렉션에서 지울 구간을 찾는다")
    @ParameterizedTest
    @CsvSource({"1,true", "3,true", "4,false"})
    void findDelete(int stationId, boolean expected) {
        // given
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, 1L, 1L, 0),
                new Section(2L, 1L, 2L, 5),
                new Section(3L, 1L, 3L, 10)
        ));

        // when
        Optional<Section> result = sections.findSectionToDeleteBy(stationId);

        // then
        assertThat(result.isPresent()).isEqualTo(expected);
    }

    @DisplayName("구간 일급 컬렉션이 초기상태(size = 2)라면 구간을 삭제할 수 없다")
    @Test
    void findDeleteFail() {
        // given
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, 1L, 1L, 0),
                new Section(2L, 1L, 2L, 5)
        ));

        // then
        assertThatIllegalStateException()
                // when
                .isThrownBy(() -> sections.findSectionToDeleteBy(1L))
                .withMessage("해당 노선은 지하철역을 삭제할 수 없습니다");
    }
}
