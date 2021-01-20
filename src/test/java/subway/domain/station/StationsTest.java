package subway.domain.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class StationsTest {
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 광교역 = new Station(3L, "광교역");
    private final Station 망포역 = new Station(4L, "망포역");

    private Stations stations;

    @BeforeEach
    public void setUp() {
        stations = new Stations(Arrays.asList(강남역, 광교역));
    }

    @Test
    @DisplayName("Station 포함 여부 테스트")
    public void containTest() {
        assertThat(stations.contain(강남역.getId())).isTrue();
        assertThat(stations.contain(광교역.getId())).isTrue();
        assertThat(stations.contain(역삼역.getId())).isFalse();
        assertThat(stations.contain(망포역.getId())).isFalse();
    }

    @Test
    @DisplayName("StationResponse 변환 테스트")
    public void toStationResponseTest() {
        List<StationResponse> stationResponses = Arrays.asList(new StationResponse(강남역), new StationResponse(광교역));
        assertThat(stations.toResponse()).isEqualTo(stationResponses);
    }
}
