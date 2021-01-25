package subway.domain.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import subway.domain.station.Station;
import subway.exception.section.SectionAttachException;
import subway.exception.section.SectionDistanceException;
import subway.exception.section.SectionSplitException;
import subway.exception.section.StationDuplicationException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class SectionTest {
    private static final Long LINE_ID = 1L;
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 역삼역 = new Station(2L, "역삼역");
    private static final Station 광교역 = new Station(3L, "광교역");
    private static final Station 망포역 = new Station(4L, "망포역");

    private static final Section 강남_망포 = new Section(1L, LINE_ID, 강남역, 망포역, 10);
    private static final Section 강남_역삼 = new Section(2L, LINE_ID, 강남역, 역삼역, 2);
    private static final Section 광교_망포 = new Section(3L, LINE_ID, 광교역, 망포역, 2);
    private static final Section 역삼_광교 = new Section(4L, LINE_ID, 역삼역, 광교역, 5);

    @Test
    @DisplayName("동일한 Station을 upStation, downStation으로 Section 생성")
    public void createSectionWithDuplicatedStation() {
        assertThatThrownBy(() -> new Section(1L, LINE_ID, 강남역, 강남역, 10))
                .isInstanceOf(StationDuplicationException.class);
    }

    @Test
    @DisplayName("Section의 거리가 1보다 작게 생성")
    public void createSectionWithNegativeDistance() {
        assertThatThrownBy(() -> new Section(1L, LINE_ID, 강남역, 역삼역, 0))
                .isInstanceOf(SectionDistanceException.class);
    }

    @DisplayName("Section 분할 테스트")
    @ParameterizedTest
    @MethodSource("provideSectionForExcludeTest")
    public void excludeSectionTest(Section section, Section sectionToDelete, Section excludedSection) {
        assertThat(section.exclude(sectionToDelete)).isEqualTo(excludedSection);
    }

    private static Stream<Arguments> provideSectionForExcludeTest() {
        return Stream.of(
                Arguments.of(강남_망포, 강남_역삼, new Section(null, LINE_ID, 역삼역, 망포역, 8)),
                Arguments.of(강남_망포, 광교_망포, new Section(null, LINE_ID, 강남역, 광교역, 8))
        );
    }

    @DisplayName("Section 분할 시 예외")
    @Test
    public void failToExclude() {
        assertThatThrownBy(() -> 강남_망포.exclude(역삼_광교)).isInstanceOf(SectionSplitException.class);
    }

    @Test
    @DisplayName("Section 병합 테스트")
    public void attachSectionTest() {
        Section newSection = 강남_역삼.attach(역삼_광교);
        assertThat(newSection).isEqualTo(new Section(null, LINE_ID, 강남역, 광교역, 7));
    }

    @Test
    @DisplayName("Section 병합 시 예외")
    public void failToAttach() {
        assertThatThrownBy(() -> 강남_역삼.attach(강남_망포)).isInstanceOf(SectionAttachException.class);
    }

    @DisplayName("해당 ID를 가지는 station을 포함하는지 테스트")
    @ParameterizedTest
    @MethodSource("provideSectionForContainTest")
    public void containTest(Section section, Station station, boolean result) {
        assertThat(section.contain(station.getId())).isEqualTo(result);
    }

    private static Stream<Arguments> provideSectionForContainTest() {
        return Stream.of(
                Arguments.of(강남_역삼, 강남역, true),
                Arguments.of(강남_역삼, 역삼역, true),
                Arguments.of(강남_역삼, 망포역, false),
                Arguments.of(강남_역삼, 광교역, false)
        );
    }
}
