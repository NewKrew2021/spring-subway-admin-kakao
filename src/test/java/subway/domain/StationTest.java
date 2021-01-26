package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import subway.exception.IllegalStationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StationTest {
    Station station;

    @BeforeEach
    void setUp() {
        station = new Station(1L, "잠실");
    }

    @ParameterizedTest
    @CsvSource({"1,true", "2,false", "3,false"})
    void 역_비교_테스트(Long id, boolean result) {
        assertThat(station.equals(new Station(id))).isEqualTo(result);
    }

    @Test
    void 역_예외_테스트() {
        assertThatExceptionOfType(IllegalStationException.class).isThrownBy(() -> new Station(-1L));
    }
}
