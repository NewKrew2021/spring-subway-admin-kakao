package subway.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.station.Station;
import subway.domain.station.Stations;
import subway.exception.section.InvalidStationException;
import subway.exception.section.SectionDeletionException;
import subway.exception.section.SectionSplitException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class SectionsTest {
    private final Long LINE_ID = 1L;
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 광교역 = new Station(3L, "광교역");
    private final Station 망포역 = new Station(4L, "망포역");

    private final Section 강남_역삼 = new Section(1L, LINE_ID, 강남역, 역삼역, 5);
    private final Section 역삼_광교 = new Section(2L, LINE_ID, 역삼역, 광교역, 5);
    private final Section 광교_망포 = new Section(3L, LINE_ID, 광교역, 망포역, 5);
    private final Section 광교_강남 = new Section(4L, LINE_ID, 광교역, 강남역, 3);
    private final Section 망포_광교 = new Section(5L, LINE_ID, 망포역, 광교역, 3);

    private Sections sections;

    @BeforeEach
    public void setUp() {
        List<Section> sectionList = Arrays.asList(강남_역삼, 역삼_광교);
        sections = new Sections(sectionList);
    }

    @Test
    @DisplayName("섹션에 포함된 지점 테스트")
    public void getAllStationTest() {
        assertThat(sections.getAllStations().contain(강남역.getId())).isTrue();
        assertThat(sections.getAllStations().contain(역삼역.getId())).isTrue();
        assertThat(sections.getAllStations().contain(광교역.getId())).isTrue();
        assertThat(sections.getAllStations().contain(망포역.getId())).isFalse();
    }

    @Test
    @DisplayName("섹션 생성 시 정렬 테스트")
    public void orderSectionTest() {
        Stations expectedStations = new Stations(Arrays.asList(강남역, 역삼역, 광교역));
        Stations unorderedStations = new Stations(Arrays.asList(강남역, 광교역, 역삼역));
        assertThat(sections.getAllStations()).isEqualTo(expectedStations);
        assertThat(sections.getAllStations()).isNotEqualTo(unorderedStations);

        List<Section> sectionList = Arrays.asList(역삼_광교, 강남_역삼);
        assertThat(new Sections(sectionList).getAllStations()).isEqualTo(expectedStations);
        assertThat(new Sections(sectionList).getAllStations()).isNotEqualTo(unorderedStations);
    }

    @Test
    @DisplayName("노선 시작 지점 테스트")
    public void findFirstStationTest() {
        assertThat(sections.findFirstStation()).isEqualTo(강남역.getId());

        List<Section> sectionList = Arrays.asList(역삼_광교, 강남_역삼);
        assertThat(new Sections(sectionList).findFirstStation()).isEqualTo(강남역.getId());
    }

    @Test
    @DisplayName("요청에 의한 새로운 섹션 생성 가능 여부 테스트")
    public void validateSectionRequestTest() {
        // 2개 역 모두 포함돼있을 경우
        assertThatThrownBy(() -> sections.validateSectionSplit(광교_강남)).isInstanceOf(SectionSplitException.class);
        // 2개 역 모두 포함되지 않을 경우
        List<Section> sectionList = Arrays.asList(강남_역삼);
        assertThatThrownBy(() -> new Sections(sectionList).validateSectionSplit(광교_망포)).isInstanceOf(SectionSplitException.class);
    }

    @Test
    @DisplayName("새로운 섹션 생성될 때 분할되는 섹션 테스트")
    public void findSectionToSplitTest() {
        List<Section> sectionList = Arrays.asList(역삼_광교, 광교_망포);
        assertThat(new Sections(sectionList).findSectionToSplit(광교_강남).orElse(null)).isEqualTo(광교_망포);

        sectionList = Arrays.asList(강남_역삼, 역삼_광교);
        assertThat(new Sections(sectionList).findSectionToSplit(망포_광교).orElse(null)).isEqualTo(역삼_광교);

        assertThat(sections.findSectionToSplit(광교_망포)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Sections 내 Station 존재 여부 테스트")
    public void containsStationTest() {
        assertThat(sections.contain(강남역.getId())).isTrue();
        assertThat(sections.contain(역삼역.getId())).isTrue();
        assertThat(sections.contain(광교역.getId())).isTrue();

        assertThat(sections.contain(망포역.getId())).isFalse();
    }

    @Test
    @DisplayName("해당 station ID를 upStation으로 가진 섹션 테스트")
    public void getSectionsFromUpStationIdTest() {
        assertThat(sections.getSectionFromUpStationId(강남역.getId()).orElse(null)).isEqualTo(강남_역삼);
        assertThat(sections.getSectionFromUpStationId(역삼역.getId()).orElse(null)).isEqualTo(역삼_광교);
        assertThat(sections.getSectionFromUpStationId(광교역.getId()).orElse(null)).isEqualTo(null);
    }

    @Test
    @DisplayName("해당 station ID를 downStation으로 가진 섹션 테스트")
    public void getSectionsFromDownStationIdTest() {
        assertThat(sections.getSectionFromDownStationId(강남역.getId()).orElse(null)).isEqualTo(null);
        assertThat(sections.getSectionFromDownStationId(역삼역.getId()).orElse(null)).isEqualTo(강남_역삼);
        assertThat(sections.getSectionFromDownStationId(광교역.getId()).orElse(null)).isEqualTo(역삼_광교);
    }

    @Test
    @DisplayName("sections에서 station을 지울 수 있는 지 테스트")
    public void validateDeleteTest() {
        assertThatThrownBy(() -> sections.validateDeleteSection(망포역.getId())).isInstanceOf(InvalidStationException.class);

        List<Section> sectionList = Arrays.asList(강남_역삼);
        assertThatThrownBy(() -> new Sections(sectionList).validateDeleteSection(강남역.getId())).isInstanceOf(SectionDeletionException.class);
    }
}
