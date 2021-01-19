package subway.station;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StationTest {
    @Test
    void testCreated() {
        assertThat(new Station("hi")).isEqualTo(new Station("hi"));
        assertThat(new Station(1L, "hi")).isEqualTo(new Station(1L, "hi"));
    }

    @Test
    void cannotCreate() {
        assertThatThrownBy(() -> new Station("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Station(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Station(-1L, "hi")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testToDto() {
        StationResponse response = new Station(1L, "hi").toDto();

        assertThat(response.getID()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("hi");
    }
}
