package subway.station.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 역 묶음 관련 기능")
class StationsTest {

    @DisplayName("지하철 역 아이디를 전달받으면, 그 순서에 따라 정렬된 지하철 역 묶음을 반환한다.")
    @Test
    void sortByIds() {
        // given
        Station first = new Station(1L, "first");
        Station second = new Station(2L, "second");
        Station third = new Station(3L, "third");
        Stations unordered = new Stations(Arrays.asList(second, first, third));

        // then
        assertThat(unordered.sortByIds(Arrays.asList(1L, 2L, 3L))).isEqualTo(
                new Stations(Arrays.asList(first, second, third)));
    }
}
