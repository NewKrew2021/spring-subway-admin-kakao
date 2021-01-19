package subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    Line line;

    @BeforeEach
    void setUp() {
        line = new Line(5, "신분당선", "bg-red-600", 2, 4);
    }

    @DisplayName("특정 역이 이 노선의 출발역인가")
    @Test
    void isStartStation() {
        long startStationId = 2;

        assertThat(line.isLineStartStation(startStationId)).isTrue();
    }

    @DisplayName("특정 역이 이 노선의 종역인가")
    @Test
    void isEndStation() {
        long endStationId = 4;

        assertThat(line.isLineEndStation(endStationId)).isTrue();
    }
}
