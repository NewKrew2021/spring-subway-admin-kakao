package subway.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {
    private Section section;

    @BeforeEach
    void setUp() {
        section = Section.of(1L, 1L, 2L, 10);
    }

    @DisplayName("같은 구간인지 확인한다.")
    @Test
    void isSameSection() {
        assertThat(section.isSameSection(Section.of(1L, 1L, 2L, 10))).isTrue();
    }

    @DisplayName("같은 역을 포함하는지 확인한다.")
    @Test
    void containStation() {
        assertThat(section.containStation(Section.of(1L, 2L, 3L, 10))).isTrue();
        assertThat(section.containStation(Section.of(1L, 3L, 2L, 10))).isTrue();
        assertThat(section.containStation(Section.of(1L, 3L, 4L, 10))).isFalse();
    }

}
