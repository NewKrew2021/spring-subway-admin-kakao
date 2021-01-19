package subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    @DisplayName("구간이 해당 역에 해당하는지 여부를 확인한다")
    @ParameterizedTest
    @CsvSource({"1,true", "2,false", "0,false"})
    void hasStation(long stationId, boolean expected) {
        // given
        Section section = new Section(1L, 1L, 0);

        // when
        boolean result = section.hasStation(stationId);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("두 개 구간의 위치 차이의 절대값을 구한다")
    @ParameterizedTest
    @CsvSource({"0,10,10", "-10,0,10", "-5,5,10"})
    void positionDifference(int position1, int position2, int expected) {
        // given
        Section first = new Section(1L, 1L, position1);
        Section second = new Section(1L, 2L, position2);

        // when
        int result = first.getDifferenceOfPosition(second);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("주어진 구간에서 상행 방향으로 주어진 거리만큼 이동하면 어떤 포지션이 나오는지 구한다")
    @Test
    void calculateNextUpPosition() {
        // given
        Section section = new Section(1L, 1L, 5);

        // when
        int result = section.calculateNextUpPosition(10);

        // then
        assertThat(result).isEqualTo(-5);
    }

    @DisplayName("주어진 구간에서 하행 방향으로 주어진 거리만큼 이동하면 어떤 포지션이 나오는지 구한다")
    @Test
    void calculateNextDownPosition() {
        // given
        Section section = new Section(1L, 1L, 5);

        // when
        int result = section.calculateNextDownPosition(10);

        // then
        assertThat(result).isEqualTo(15);
    }
}
