package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("구간 유닛 테스트")
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

    @Test
    @DisplayName("구간 비교 테스트")
    void sectionEqualTest() {
        Section section = new Section(new Station(1L), new Station(2L), 1, 2L);
        Section section2 = new Section(new Station(1L), new Station(2L), 5, 2L);
        assertThat(section.equals(section2)).isEqualTo(true);
    }
}
