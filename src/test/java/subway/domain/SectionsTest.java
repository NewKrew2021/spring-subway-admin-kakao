package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import subway.domain.Section;
import subway.domain.Sections;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {
    Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(Arrays.asList(new Section(1L, 2L, 3)));
    }

    @Test
    void findSectionByDownId() {
        assertThat(sections.findSectionByDownStationId(2L)).isEqualTo(new Section(1L, 2L, 3));
    }

    @Test
    void findSectionByUpId() {
        assertThat(sections.findSectionByUpStationId(1L)).isEqualTo(new Section(1L, 2L, 3));
    }

    @Test
    void findSectionByDownIdNull() {
        assertThat(sections.findSectionByDownStationId(1L)).isNull();
    }

    @Test
    void findSectionByUpIdNull() {
        assertThat(sections.findSectionByUpStationId(2L)).isNull();
    }
}
