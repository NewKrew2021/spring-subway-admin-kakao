package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import subway.line.Line;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(Arrays.asList(new Section(1L, 2L, 3), new Section(2L, 3L, 4)));
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
        assertThat(sections.findSectionByUpStationId(3L)).isNull();
    }

    @Test
    void orderLoopTest(){
        Sections sections = new Sections(Arrays.asList(new Section(1L, 2L, 3), new Section(2L, 1L, 4)));
        assertThatThrownBy(() -> {
            sections.getOrderedSection(new Line("line1", "red", 1L, 3L));
        }).isInstanceOf(RuntimeException.class);
    }
}
