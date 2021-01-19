package subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exceptions.InvalidLineArgumentException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineRequestTest {

    @DisplayName("필수 입력값이 비어있는가")
    @Test
    void nonemptyArgument() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 0, 5, 0);

        assertThatThrownBy(() -> {
            lineRequest.validateLineRequest();
        }).isInstanceOf(InvalidLineArgumentException.class);
    }

    @DisplayName("상행 종점과 하행 종점이 같은 역으로 선택됐는가")
    @Test
    void sameUpAndDown() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 5, 5, 50);

        assertThatThrownBy(() -> {
            lineRequest.validateLineRequest();
        }).isInstanceOf(InvalidLineArgumentException.class);
    }
}
