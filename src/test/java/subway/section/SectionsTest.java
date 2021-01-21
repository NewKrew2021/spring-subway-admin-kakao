package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import subway.line.Line;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    static Stream<Arguments> sectionsGenerator() {
        return Stream.of(
                Arguments.of(Arrays.asList(
                        new Section(0L, 0L, Line.HEADID, 1L, 3),
                        new Section(0L, 0L, 1L, 2L, 3),
                        new Section(0L, 0L, 2L, 3L, 3),
                        new Section(0L, 0L, 3L, 4L, 3),
                        new Section(0L, 0L, 4L, Line.TAILID, 3)
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("sectionsGenerator")
    @DisplayName("주어진 섹션들의 집합에서 가장 앞의 섹션 찾기")
    public void findFirst(List<Section> sectionList) {
        Sections sections = new Sections(sectionList);
        Section headSection = sections.findHeadSection();
        assertThat(headSection).isEqualTo(new Section(0L, 0L, Line.HEADID, 1L, 3));
    }

    @ParameterizedTest
    @MethodSource("sectionsGenerator")
    @DisplayName("주어진 스테이션의 바로 뒤 섹션 찾기 테스트")
    public void findLast(List<Section> sectionList) {
        Sections sections = new Sections(sectionList);
        Section nextSection = sections.findRearByStation(2L);
        assertThat(nextSection).isEqualTo(new Section(0L, 0L, 2L, 3L, 3));
    }

    @ParameterizedTest
    @MethodSource("sectionsGenerator")
    @DisplayName("주어진 스테이션의 바로 앞 섹션 찾기 테스트")
    public void findFront(List<Section> sectionList) {
        Sections sections = new Sections(sectionList);
        Section nextSection = sections.findFrontByStation(2L);
        assertThat(nextSection).isEqualTo(new Section(0L, 0L, 1L, 2L, 3));
    }

    @ParameterizedTest
    @MethodSource("sectionsGenerator")
    @DisplayName("동일한 섹션이 존재하는지 테스트 1 : 완전히 일치하는 섹션 검사")
    public void findDuplicate(List<Section> sectionList) {
        // given
        Sections sections = new Sections(sectionList);

        // when
        boolean actual = sections.hasSameSection(new Section(0L, 0L, 1L, 2L, 3));

        // then
        assertThat(actual).isEqualTo(true);
    }

    @ParameterizedTest
    @MethodSource("sectionsGenerator")
    @DisplayName("동일한 섹션이 존재하는지 테스트 2 : 방향이 달라도 겹치는지 검사")
    public void findDuplicate2(List<Section> sectionList) {
        // given
        Sections sections = new Sections(sectionList);

        // when
        boolean actual = sections.hasSameSection(new Section(0L, 0L, 2L, 1L, 3));

        // then
        assertThat(actual).isEqualTo(true);
    }

    @ParameterizedTest
    @MethodSource("sectionsGenerator")
    @DisplayName("동일한 섹션이 존재하는지 테스트 3 : 유효하지 않은 섹션인지 검사")
    public void findDuplicate3(List<Section> sectionList) {
        // given
        Sections sections = new Sections(sectionList);

        // when
        boolean actual = sections.hasSameSection(new Section(0L, 0L, 1L, 2L, 6));

        // then
        assertThat(actual).isEqualTo(true);
    }
}
