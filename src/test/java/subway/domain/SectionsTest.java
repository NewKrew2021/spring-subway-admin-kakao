package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sections 도메인 관련 기능")
class SectionsTest {
    private Section firstSection;
    private Section middleSection;
    private Section lastSection;
    private Sections sections;

    @BeforeEach
    void setUp() {
        firstSection = new Section(1L, 1L, 2L, 10, true, false);
        middleSection = new Section(1L, 2L, 3L, 10, false, false);
        lastSection = new Section(1L, 3L, 4L, 10, false, true);
        sections = new Sections(Arrays.asList(firstSection, middleSection, lastSection));
    }

    @DisplayName("상행역을 키로 갖는 맵을 생성한다.")
    @Test
    void getUpStationKeyMap() {
        Map<Long, Section> upStationKeyMap = sections.getUpStationKeyMap();

        assertThat(upStationKeyMap).containsKeys(1L, 2L, 3L);
    }

    @DisplayName("하행역을 키로 갖는 맵을 생성한다.")
    @Test
    void getDownStationKeyMap() {
        Map<Long, Section> downStationKeyMap = sections.getDownStationKeyMap();

        assertThat(downStationKeyMap).containsKeys(2L, 3L, 4L);
    }

    @DisplayName("상행종점을 확인한다.")
    @Test
    void getFirstSection() {
        Section firstSection = sections.getFirstSection();

        assertThat(firstSection).isEqualTo(this.firstSection);
    }

    @DisplayName("하행종점을 확인한다.")
    @Test
    void getLastSection() {
        Section lastSection = sections.getLastSection();

        assertThat(lastSection).isEqualTo(this.lastSection);
    }

    @DisplayName("중간에 있는 역 삭제 시 두 구간을 병합한다.")
    @Test
    void getContainedSections() {
        Long stationId = 2L;
        Sections containedSections = sections.getContainedSections(stationId);

        assertThat(containedSections.getMergeSection(stationId))
                .isEqualTo(new Section(1L, 1L, 3L, 20, true, false));
        assertThat(containedSections.getDeleteSection()).isEqualTo(middleSection);
    }

    @DisplayName("하행종점 삭제 시 이전 역을 업데이트 한다.")
    @Test
    void getPreviousSection() {
        Section previousSection = sections.getPreviousSection(lastSection);

        assertThat(previousSection).isEqualTo(new Section(1L, 2L, 3L, 10, false, true));
    }

    @DisplayName("상행종점 삭제 시 다음 역을 업데이트 한다.")
    @Test
    void getNextSection() {
        Section nextSection = sections.getNextSection(firstSection);

        assertThat(nextSection).isEqualTo(new Section(1L, 2L, 3L, 10, true, false));
    }

    @DisplayName("중간에 새로운 구간 추가 시 두 구간을 분리한다.")
    @Test
    void getSeparatedSections2() {
        Section newSection = new Section(1L, 5L, 2L, 7, false, false);
        Sections separatedSections = sections.getSeparatedSections(newSection);

        assertThat(separatedSections.getNewSection())
                .isEqualTo(new Section(1L, 5L, 2L, 7, false, false));

        assertThat(separatedSections.getUpdateSection())
                .isEqualTo(new Section(1L, 1L, 5L, 3, true, false));
    }
}