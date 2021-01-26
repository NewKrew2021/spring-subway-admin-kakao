package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("라인 유닛 테스트")
public class LineTest {
    Sections sections;
    Station 잠실;
    Station 분당;
    Station 판교;
    Station 정자;
    Station 야탑;
    Station 광교;
    Line line;

    @BeforeEach
    void setUp() {
        잠실 = new Station(1L, "잠실");
        분당 = new Station(2L, "분당");
        판교 = new Station(3L, "판교");
        정자 = new Station(4L, "정자");
        야탑 = new Station(5L, "야탑");
        광교 = new Station(6L, "광교");
        sections = new Sections(Arrays.asList(new Section(잠실, 분당, 5, 1L), new Section(분당, 판교, 5, 1L)));
        line = new Line(1L, "신분당선", "red", sections);
    }

    @Test
    @DisplayName("역 가져오기 테스트")
    void getStationTest() {
        assertThat(line.getStations()).containsExactly(잠실, 분당, 판교);
    }

    @Test
    @DisplayName("섹션 추가 테스트")
    void addSectionTest() {
        line.addSection(판교, 정자, 1);
        assertThat(line.getStations()).containsExactly(잠실, 분당, 판교, 정자);
    }
}
