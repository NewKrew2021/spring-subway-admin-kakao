package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {
    Sections sections;
    Station 잠실;
    Station 분당;
    Station 판교;
    @BeforeEach
    void setUp() {
        잠실 = new Station(1L,"잠실");
        분당 = new Station(2L,"분당");
        판교 = new Station(3L,"판교");
        sections = new Sections(Arrays.asList(new Section(잠실, 분당, 3)));
    }

    @Test
    void findSectionByDownId() {
        assertThat(sections.findSectionByDownStation(분당)).isEqualTo(new Section(잠실, 분당, 3));
    }

    @Test
    void findSectionByUpId() {
        assertThat(sections.findSectionByUpStation(잠실)).isEqualTo(new Section(잠실, 분당, 3));
    }

    @Test
    void findSectionByDownIdNull() {
        assertThat(sections.findSectionByDownStation(잠실)).isNull();
    }

    @Test
    void findSectionByUpIdNull() {
        assertThat(sections.findSectionByUpStation(분당)).isNull();
    }
}
