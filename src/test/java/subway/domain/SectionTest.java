package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Section 도메인 관련 기능")
class SectionTest {
    private Section firstSection;
    private Section lastSection;

    @BeforeEach
    void setUp() {
        firstSection = new Section(1L, 1L, 2L, 10, true, false);
        lastSection = new Section(1L, 2L, 3L, 10, false, true);
    }

    @DisplayName("새로운 하행종점인지 확인한다.")
    @Test
    void isNewLastSection() {
        Section newSection = new Section(1L, 3L, 4L, 10, false, false);

        boolean newLastSection = newSection.isNewLastSection(lastSection);

        assertThat(newLastSection).isTrue();
    }

    @DisplayName("새로운 상행종점인지 확인한다.")
    @Test
    void isNewFirstSection() {
        Section newSection = new Section(1L, 4L, 1L, 10, false, false);

        boolean newFirstSection = newSection.isNewFirstSection(firstSection);

        assertThat(newFirstSection).isTrue();
    }

    @DisplayName("구간을 병합한다.")
    @Test
    void merge() {
        Section mergedSection = firstSection.merge(lastSection, 2L);

        assertThat(mergedSection).isEqualTo(new Section(1L, 1L, 3L, 20, true, true));
    }

    @DisplayName("구간의 상행역이 주어진 역과 동일한지 확인한다.")
    @Test
    void equalsWithUpStation() {
        boolean equalsWithUpStation = firstSection.equalsWithUpStation(1L);

        assertThat(equalsWithUpStation).isTrue();
    }

    @DisplayName("구간의 하행역이 주어진 역과 동일한지 확인한다.")
    @Test
    void equalsWithDownStation() {
        boolean equalsWithDownStation = firstSection.equalsWithDownStation(2L);

        assertThat(equalsWithDownStation).isTrue();
    }
}