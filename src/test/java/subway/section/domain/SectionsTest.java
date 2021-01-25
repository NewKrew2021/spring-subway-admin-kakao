package subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import subway.section.presentation.SectionRequest;

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

    @DisplayName("초기 구간은 2개의 구간으로 이루어진다")
    @Test
    void initialize() {
        // given
        long lineId = 1L;
        SectionRequest request = new SectionRequest(2L, 3L, 5);
        SectionCreateValue sectionValue = request.toCreateValue(lineId);

        // when
        Sections result = Sections.initialize(sectionValue);

        // then
        assertThat(result).extracting("sections")
                .usingRecursiveComparison()
                .isEqualTo(Arrays.asList(
                        new Section(lineId, request.getUpStationId(), 0),
                        new Section(lineId, request.getDownStationId(), 5)
                ));
    }

    @DisplayName("(다음역 - 역) 상행방향 구간의 거리가 주어진 거리보다 작거나 같으면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {5, 100})
    void validateSectionUpDistance(int distance) {
        // given
        long lineId = 1L;
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, lineId, 1L, 0),
                new Section(2L, lineId, 2L, 5)
        ));
        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> sections.validateSectionUpDistance(sections.getSections().get(1), distance))
                .withMessage("기존 구간보다 새로 생긴 구간의 거리가 더 짧아야합니다");
    }

    @DisplayName("(역 - 다음역) 하행방향 구간의 거리가 주어진 거리보다 작거나 같으면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {5, 100})
    void validateSectionDownDistance(int distance) {
        // given
        long lineId = 1L;
        Sections sections = Sections.from(Arrays.asList(
                new Section(1L, lineId, 1L, 0),
                new Section(2L, lineId, 2L, 5)
        ));
        // then
        assertThatIllegalArgumentException()
                // when
                .isThrownBy(() -> sections.validateSectionDownDistance(sections.getSections().get(0), distance))
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
