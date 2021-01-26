package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.exceptions.InvalidSectionException;
import subway.line.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Sections sections;
    private SectionRequest sectionRequest1;
    private SectionRequest sectionRequest2;
    private SectionRequest sectionRequest3;
    private SectionRequest sectionRequest4;
    private Section section1;
    private Section section2;
    private Section section3;
    private Section section4;

    @BeforeEach
    void setUp() {
        List<Section> allSection = new ArrayList<>();

        section1 = new Section(1, 1, 2, 4, 10);
        section2 = new Section(2, 1, 4, 8, 15);
        section3 = new Section(3, 1, 8, 6, 20);
        section4 = new Section(4, 1, 6, 12, 10);

        allSection.add(section1);
        allSection.add(section2);
        allSection.add(section3);
        allSection.add(section4);
        sections = new Sections(allSection);

        sectionRequest1 = new SectionRequest(4, 10, 5);
        sectionRequest2 = new SectionRequest(2, 8, 5);
        sectionRequest3 = new SectionRequest(14, 12, 5);
        sectionRequest4 = new SectionRequest(10, 14, 5);
    }

    @DisplayName("다음 구간의 상행역")
    @Test
    void getNextUpStationId() {
        long nextUpStationId = sections.nextUpStationId(4);

        assertThat(nextUpStationId).isEqualTo(8);
    }

    @DisplayName("추가할 구간의 두 역이 이미 존재함")
    @Test
    void alreadyExistBothStations() {
        assertThatThrownBy(() -> {
            sections.addSection(sectionRequest2.toSection());
        }).isInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("추가할 구간의 두 역이 모두 존재하지 않음")
    @Test
    void nothingStation() {
        assertThatThrownBy(() -> {
            sections.addSection(sectionRequest4.toSection());
        }).isInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("새 구간을 추가함")
    @Test
    void addNewSection() {
        Section newSection = new Section(5, 1, 12, 20, 50);

        sections.addSection(newSection);

        assertThat(sections.getSections()).contains(newSection);
    }

    @DisplayName("기존 구간 사이에 새 구간을 추가함")
    @Test
    void addNewSectionBetweenSections() {
        sections.addSection(sectionRequest1.toSection());

        assertThat(sections.getSections()).contains(sectionRequest1.toSection());
        assertThat(sections.getSections()).doesNotContain(section2);
    }

    @DisplayName("특정 구간 상행역 기준으로 새 구간이 추가될 때 다음 구간이 변경되는가")
    @Test
    void findUpdatedNextSection() {
        Section expectedSection = new Section(10, 8, 10);

        sections.addSection(sectionRequest1.toSection());

        assertThat(sections.getSections()).contains(expectedSection);
    }

    @DisplayName("특정 구간 하행역 기준으로 새 구간이 추가될 때 이전 구간이 변경되는가")
    @Test
    void findUpdatedPreviousSection() {
        Section expectedSection = new Section(6, 14, 5);

        sections.addSection(sectionRequest3.toSection());

        assertThat(sections.getSections()).contains(expectedSection);
    }

    @DisplayName("노선에 구간이 한 개 존재함")
    @Test
    void onlyOneSection() {
        Sections sections2 = new Sections(Arrays.asList(section1));

        assertThatThrownBy(() -> {
            sections2.deleteSection(2);
        }).isInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("노선에서 끝에 존재하는 역을 제거함")
    @Test
    void deleteEndpoint() {
        sections.deleteSection(2);

        assertThat(sections.getSections()).doesNotContain(section1);
    }

    @DisplayName("노선에서 중간에 존재하는 역을 제거함")
    @Test
    void deleteBetweenSections() {
        Section expectedSection = new Section(4, 6, 35);

        sections.deleteSection(8);

        assertThat(sections.getSections()).doesNotContain(section2);
        assertThat(sections.getSections()).doesNotContain(section3);
        assertThat(sections.getSections()).contains(expectedSection);
    }
}
