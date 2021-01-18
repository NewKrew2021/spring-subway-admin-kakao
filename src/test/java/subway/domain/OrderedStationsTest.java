package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상행부터 하행 순으로 나열된 역 테스트")
public class OrderedStationsTest {
    private OrderedStations orderedStations;

    @BeforeEach
    public void createOrderedStations() {
        OrderedSections orderedSections = new OrderedSections(
                Arrays.asList(
                        new Section(1L, 4L, 5L, 6),
                        new Section(1L, 1L, 2L, 3),
                        new Section(1L, 3L, 4L, 5),
                        new Section(1L, 2L, 3L, 4)
                )
        );

        orderedStations = new OrderedStations(orderedSections, Arrays.asList(
                new Station(3L, "역삼역"),
                new Station(2L, "논현역"),
                new Station(4L, "서현역"),
                new Station(1L, "강남역"),
                new Station(5L, "정자역"))
        );
    }

    @DisplayName("상행부터 하행 순으로 나열된 지하철역 받아오기.")
    @Test
    public void getOrderedStationsTest() {
        assertThat(orderedStations.getOrderedStations()).containsExactlyElementsOf(
                Arrays.asList(
                        new Station(1L, "강남역"),
                        new Station(2L, "논현역"),
                        new Station(3L, "역삼역"),
                        new Station(4L, "서현역"),
                        new Station(5L, "정자역")
                )
        );
    }

    @DisplayName("지하철역을 포함하고 있는지 확인하기.")
    @Test
    public void hasStationTest() {
        assertThat(orderedStations.hasStation(1L)).isTrue();
        assertThat(orderedStations.hasStation(6L)).isFalse();
    }
}
