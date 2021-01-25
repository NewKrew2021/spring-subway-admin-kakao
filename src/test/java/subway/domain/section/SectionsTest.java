package subway.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Section;
import subway.domain.Sections;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    private Sections sections;
    private Sections sectionsWithOneSection;

    @BeforeEach
    void setUp() {
        sections = new Sections(Arrays.asList(
                Section.of(1L, 2L, 3L, 5),
                Section.of(1L, 1L, 2L, 10),
                Section.of(1L, 3L, 5L, 10)
        ));
        sectionsWithOneSection = new Sections(Collections.singletonList(
                Section.of(1L, 1L, 2L, 10)
        ));
    }

    @DisplayName("같은 구역을 포함하는지 확인한다.")
    @Test
    void checkSameSection() {
        assertThatThrownBy(() -> sections.checkSameSection(Section.of(1L, 2L, 3L, 7)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("일치하는 역이 없는지 확인한다.")
    @Test
    void checkNoStation() {
        assertThatThrownBy(() -> sections.checkNoStation(Section.of(1L, 4L, 6L, 5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간이 하나인지 확인한다.")
    @Test
    void checkOneSection() {
        assertThatThrownBy(() -> sectionsWithOneSection.checkOneSection()).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("첫번째 구간인지 확인한다.")
    @Test
    void isFirstSection() {
        assertThat(sections.isFirstSection(Section.of(1L, 6L, 1L, 20))).isTrue();
    }

    @DisplayName("마지막 구간인지 확인한다.")
    @Test
    void isLastSection() {
        assertThat(sections.isLastSection(Section.of(1L, 5L, 10L, 10))).isTrue();
    }

    @DisplayName("상행역이 일치하는 구간을 비교한다.")
    @Test
    void getMatchedUpStation() {
        assertThat(sections.getMatchedUpStation(
                Section.of(1L, 3L, 4L, 3)).isSameSection(
                Section.of(1L, 3L, 5L, 10)
        )).isTrue();
    }

    @DisplayName("하행역이 일치하는 구간을 비교한다.")
    @Test
    void getMatchedDownStation() {
        assertThat(sections.getMatchedDownStation(
                Section.of(1L, 4L, 5L, 3)).isSameSection(
                Section.of(1L, 3L, 5L, 10)
        )).isTrue();
    }

    @DisplayName("역을 포함하는 구간을 확인한다.")
    @Test
    void getNeighboringSections() {
        assertThat(sections.getNeighboringSections(3L)).isEqualTo(Arrays.asList(
                Section.of(1L, 2L, 3L, 5),
                Section.of(1L, 3L, 5L, 10)
        ));
    }

    @DisplayName("역을 정렬된 상태로 조회한다.")
    @Test
    void getSortedStationIds() {
        assertThat(sections.getSortedStationIds()).isEqualTo(Arrays.asList(1L, 2L, 3L, 5L));
    }
}
