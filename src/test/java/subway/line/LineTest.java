package subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.section.Section;
import subway.section.Sections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    private Section section1;
    private Section section2;
    private Section section3;
    private Section section4;
    private Sections sections;
    private Line line;

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

        line = new Line(5, "신분당선", "bg-red-600", sections);
    }

    @DisplayName("새 구간 추가")
    @Test
    void addNewSection() {
        Section newSection = new Section(5, 1, 12, 100, 10);

        line.addSection(newSection);

        assertThat(line.getSections().getSections()).contains(newSection);
        assertThat(line.getStationIds()).contains(100L);
    }

    @DisplayName("특정 역(구간) 삭제")
    @Test
    void deleteSection() {
        line.deleteSection(4);

        assertThat(line.getSections().getSections()).doesNotContain(section2);
        assertThat(line.getStationIds()).doesNotContain(4L);
    }
}
