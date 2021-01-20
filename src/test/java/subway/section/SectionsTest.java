package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.exceptions.FailedDeleteSectionException;
import subway.exception.exceptions.FailedSaveSectionException;
import subway.exception.exceptions.InvalidSectionException;

import java.util.Arrays;

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
        section1 = new Section(1, 1, 2, 4, 10);
        section2 = new Section(2, 1, 4, 8, 15);
        section3 = new Section(3, 1, 8, 6, 20);
        section4 = new Section(4, 1, 6, 12, 10);

        sections = new Sections(Arrays.asList(section1, section2, section3, section4));

        sectionRequest1 = new SectionRequest(4, 10, 5);
        sectionRequest2 = new SectionRequest(2, 8, 5);
        sectionRequest3 = new SectionRequest(14, 12, 5);
        sectionRequest4 = new SectionRequest(10, 14, 5);
    }

    @DisplayName("구간의 개수")
    @Test
    void getSize() {
        int size = sections.size();

        assertThat(size).isEqualTo(4);
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
            sections.validateAlreadyExistBothStationsOrNothing(sectionRequest2);
        }).isInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("추가할 구간의 두 역이 모두 존재하지 않음")
    @Test
    void nothingStation() {
        assertThatThrownBy(() -> {
            sections.validateAlreadyExistBothStationsOrNothing(sectionRequest4);
        }).isInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("특정 구간 상행역 기준으로 새 구간이 추가될 때 다음 구간이 어떻게 변경되는가")
    @Test
    void findUpdatedNextSection() {
        Section expectedSection = new Section(2, 1, 10, 8, 10);

        Section updatedSection = sections.getUpdatedSection(sectionRequest1);

        assertThat(updatedSection).isEqualTo(expectedSection);
    }

    @DisplayName("특정 구간 하행역 기준으로 새 구간이 추가될 때 이전 구간이 어떻게 변경되는가")
    @Test
    void findUpdatedPreviousSection() {
        Section expectedSection = new Section(4, 1, 6, 14, 5);

        Section updatedSection = sections.getUpdatedSection(sectionRequest3);

        assertThat(updatedSection).isEqualTo(expectedSection);
    }

    @DisplayName("기존 구간을 변경할 수 없음")
    @Test
    void unableUpdateSection() {
        assertThatThrownBy(() -> {
            sections.getUpdatedSection(sectionRequest4);
        }).isInstanceOf(FailedSaveSectionException.class);
    }

    @DisplayName("노선에 구간이 한 개 존재함")
    @Test
    void onlyOneSection() {
        Sections sections2 = new Sections(Arrays.asList(section1));

        assertThatThrownBy(() -> {
            sections2.validateLineContainsOnlyOneSection();
        }).isInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("상행역 ID로 재배치할 구간 탐색")
    @Test
    void findSectionUsingUpStation() {
        Section wantSection = sections.findSectionByUpStationId(8);

        assertThat(wantSection).isEqualTo(section3);
    }

    @DisplayName("하행역 ID로 재배치할 구간 탐색")
    @Test
    void findSectionUsingDownStation() {
        Section wantSection = sections.findSectionByDownStationId(8);

        assertThat(wantSection).isEqualTo(section2);
    }

    @DisplayName("상행역 ID를 기준으로 재배치할 구간을 찾을 수 없음")
    @ParameterizedTest
    @ValueSource(longs = {10, 12})
    void unableFindDeleteSectionUsingUpStation(long id) {
        assertThatThrownBy(() -> {
            sections.findSectionByUpStationId(id);
        }).isInstanceOf(FailedDeleteSectionException.class);
    }

    @DisplayName("하행역 ID를 기준으로 재배치할 구간을 찾을 수 없음")
    @ParameterizedTest
    @ValueSource(longs = {2, 10})
    void unableFindDeleteSectionUsingDownStation(long id) {
        assertThatThrownBy(() -> {
            sections.findSectionByDownStationId(id);
        }).isInstanceOf(FailedDeleteSectionException.class);
    }
}
