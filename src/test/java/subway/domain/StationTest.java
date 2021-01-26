package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationTest {

    @DisplayName("같은 아이디")
    @Test
    void hasSameId() {
        Station station = new Station(1L, "강남역");

        boolean sameId = station.hasSameId(1L);

        assertThat(sameId).isEqualTo(true);
    }

    @DisplayName("다른 아이디")
    @Test
    void hasDifferentId() {
        Station station = new Station(2L, "양재역");

        boolean sameId = station.hasSameId(1L);

        assertThat(sameId).isEqualTo(false);
    }
}