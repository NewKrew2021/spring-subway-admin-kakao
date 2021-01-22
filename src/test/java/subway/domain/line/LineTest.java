package subway.domain.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Line;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        line = Line.of(1L, "신분당선", "red");
    }

    @DisplayName("이름과 색깔의 업데이트 동작을 확인한다.")
    @Test
    void updateNameAndColor() {
        line.updateNameAndColor("2호선", "blue");
        assertThat(line).isEqualTo(Line.of(1L, "2호선", "blue"));
    }
}
