package subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.station.domain.Stations;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {
    @Test
    void testCreated() {
        assertThat(new Line(1L, "hi", "red")).isNotNull();
    }

    @Test
    void cannotCreate() {
        assertThatThrownBy(() -> new Line(-1L, "hi", "red")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Line(1L, " ", "red")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Line(1L, "hi", "\n")).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Line에서 LineDTO를 올바르게 생성한다")
    @Test
    void testToDto() {
        LineResponse response = new Line(1L, "hi", "red").toResultValue(new Stations(Collections.emptyList()));

        assertThat(response.getID()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("hi");
        assertThat(response.getColor()).isEqualTo("red");
        assertThat(response.getStations()).isEmpty();
    }
}
