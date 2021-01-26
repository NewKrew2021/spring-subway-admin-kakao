package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.DistanceException;
import subway.exception.IllegalStationException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SectionTest {

    @Test
    @DisplayName("역번호 중복 예외 테스트")
    void createExceptionTest() {
        assertThatExceptionOfType(IllegalStationException.class).isThrownBy(() -> {
            new Section(new Station(1L), new Station(1L), 3);
        });
    }

    @Test
    @DisplayName("거리 부족 테스트")
    void createExceptionTest2() {
        assertThatExceptionOfType(DistanceException.class).isThrownBy(() -> {
            new Section(new Station(1L), new Station(2L), 0);
        });
    }
}
