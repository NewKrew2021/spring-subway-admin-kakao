package subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.section.Section;
import subway.section.Sections;
import subway.station.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

public class LineTest {
    private final Long LINE_ID = 1L;
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 광교역 = new Station(3L, "광교역");

    private final Section 강남_역삼 = new Section(1L, LINE_ID, 강남역, 역삼역, 5);
    private final Section 역삼_광교 = new Section(2L, LINE_ID, 역삼역, 광교역, 5);

    @Test
    @DisplayName("line에 속하는 station 테스트")
    public void getAllStations() {
        Sections sections = new Sections(Arrays.asList(강남_역삼, 역삼_광교));
        Line line = new Line(LINE_ID, "수인선", "red", sections);
        assertThat(line.getAllStations()).containsExactly(강남역, 역삼역, 광교역);

        sections = new Sections(Arrays.asList(역삼_광교, 강남_역삼));
        line = new Line(LINE_ID, "수인선", "red", sections);
        assertThat(line.getAllStations()).containsExactly(강남역, 역삼역, 광교역);
    }
}
