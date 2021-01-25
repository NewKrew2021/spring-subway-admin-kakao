package subway.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import subway.domain.station.Station;
import subway.domain.station.Stations;
import subway.exception.section.InvalidStationException;
import subway.exception.section.SectionDeletionException;
import subway.exception.section.SectionSplitException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class SectionsTest {
    private static final Long LINE_ID = 1L;
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 역삼역 = new Station(2L, "역삼역");
    private static final Station 광교역 = new Station(3L, "광교역");
    private static final Station 망포역 = new Station(4L, "망포역");
    private static final Station 제주역 = new Station(5L, "제주역");

    private static final Section 강남_역삼 = new Section(1L, LINE_ID, 강남역, 역삼역, 5);
    private static final Section 역삼_광교 = new Section(2L, LINE_ID, 역삼역, 광교역, 5);
    private static final Section 광교_망포 = new Section(3L, LINE_ID, 광교역, 망포역, 5);
    private static final Section 광교_강남 = new Section(4L, LINE_ID, 광교역, 강남역, 3);
    private static final Section 망포_광교 = new Section(5L, LINE_ID, 망포역, 광교역, 3);

    private Sections sections;

    @BeforeEach
    public void setUp() {
        List<Section> sectionList = Arrays.asList(강남_역삼, 역삼_광교);
        sections = new Sections(sectionList);
    }

    @DisplayName("섹션에 포함된 지점 테스트")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    public void containStation(Long stationId) {
        assertThat(sections.getAllStations().contain(stationId)).isTrue();
    }

    @DisplayName("섹션에 포함되지 않는 지점 테스트")
    @ParameterizedTest
    @ValueSource(longs = {4L, 5L})
    public void notContainStation(Long stationId) {
        assertThat(sections.getAllStations().contain(stationId)).isFalse();
    }

    @Test
    @DisplayName("섹션 생성 시 정렬 테스트")
    public void orderSectionTest() {
        Stations expectedStations = new Stations(Arrays.asList(강남역, 역삼역, 광교역));

        assertThat(sections.getAllStations()).isEqualTo(expectedStations);
    }

    @Test
    @DisplayName("섹션 생성 시 2개 역 모두 기존 포함 되있을 경우 예외 발생")
    public void containBothStation() {
        assertThatThrownBy(() -> sections.validateSectionSplit(광교_강남))
                .isInstanceOf(SectionSplitException.class);
    }

    @Test
    @DisplayName("섹션 생성 시 2개 역 모두 기존 미포함 되었을 경우 예외 발생")
    public void containNonStation() {
        List<Section> sectionList = Arrays.asList(강남_역삼);

        assertThatThrownBy(() -> new Sections(sectionList). validateSectionSplit(광교_망포))
                .isInstanceOf(SectionSplitException.class);
    }

    @DisplayName("새로운 섹션 생성될 때 분할되는 섹션 테스트")
    @ParameterizedTest
    @MethodSource("provideSectionsForSplit")
    public void findSectionToSplitTest(Section section, Section splitedSection, Section newSection) {
        Sections sections = new Sections(Arrays.asList(section, splitedSection));
        assertThat(sections.findSectionToSplit(newSection).orElse(null)).isEqualTo(splitedSection);
    }

    private static Stream<Arguments> provideSectionsForSplit() {
        return Stream.of(
                Arguments.of(역삼_광교, 광교_망포, 광교_강남),
                Arguments.of(강남_역삼, 역삼_광교, 망포_광교)
        );
    }

    @DisplayName("새로운 섹션 생성될 때 분할되는 섹션이 없다")
    @ParameterizedTest
    @MethodSource("provideSectionsForNonSplit")
    public void findNoSectionToSplit() {
        Sections sections = new Sections(Arrays.asList(강남_역삼, 역삼_광교));
        assertThat(sections.findSectionToSplit(광교_망포)).isEqualTo(Optional.empty());
    }

    private static Stream<Arguments> provideSectionsForNonSplit() {
        return Stream.of(
                Arguments.of(강남_역삼, 역삼_광교, 광교_망포),
                Arguments.of(역삼_광교, 광교_망포, 강남_역삼)
        );
    }

    @DisplayName("Sections 내 Station 존재")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    public void containsStationTest(Long stationId) {
        assertThat(sections.contain(stationId)).isTrue();
    }

    @DisplayName("Sections 내 Station 존재하지 않음")
    @ParameterizedTest
    @ValueSource(longs = {4L, 5L})
    public void notContainsStationTest(Long stationId) {
        assertThat(sections.contain(stationId)).isFalse();
    }

    @Test
    @DisplayName("sections 내에 존재하지 않는 역을 삭제")
    public void deleteInvalidStation() {
        assertThatThrownBy(() -> sections.validateDeleteSection(망포역.getId())).isInstanceOf(InvalidStationException.class);
    }

    @Test
    @DisplayName("섹션이 하나만 존재할 때 역을 삭제")
    public void deleteLastSection() {
        Sections sections = new Sections(Arrays.asList(강남_역삼));
        assertThatThrownBy(() -> sections.validateDeleteSection(강남역.getId())).isInstanceOf(SectionDeletionException.class);
    }

    @Test
    @DisplayName("sections내 section 연결 테스트")
    public void connectTest() {
        assertThat(sections.connect()).isEqualTo(new Section(LINE_ID, 강남역, 광교역, 10));
    }
}
