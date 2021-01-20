package subway.station;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StationsTest {
    @Test
    void testCreated() {
        assertThat(new Stations(Arrays.asList(
                new Station(1L, "hi"),
                new Station(2L, "hello")
        ))).isNotNull();
    }

    @Test
    void cannotCreate() {
        assertThatThrownBy(() -> new Stations(Arrays.asList(
                new Station(1L, "hi"),
                new Station(2L, "hi")
        ))).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Stations(Arrays.asList(
                new Station(1L, "hi"),
                new Station(1L, "hello")
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testAllToDto() {
        Stations stations = new Stations(Arrays.asList(
                new Station(1L, "hi"),
                new Station(2L, "hello")
        ));

        List<StationResponse> response = stations.allToDto();

        assertThat(response.get(0).getID()).isEqualTo(1L);
        assertThat(response.get(0).getName()).isEqualTo("hi");
        assertThat(response.get(1).getID()).isEqualTo(2L);
        assertThat(response.get(1).getName()).isEqualTo("hello");
    }
}
