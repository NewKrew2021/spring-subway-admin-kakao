package subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.station.dto.StationResponse;

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
        assertThatThrownBy(() -> new Station(-1L, "hi")).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Station에서 StationDTO를 올바르게 생성한다")
    @Test
    void testToDto() {
        StationResponse response = StationResponse.of(new Station(1L, "hi"));

        assertThat(response.getID()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("hi");
    }
}
