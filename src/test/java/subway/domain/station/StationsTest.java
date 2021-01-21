package subway.domain.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.station.DuplicateStationNameException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

public class StationsTest {
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 광교역 = new Station(3L, "광교역");
    private final Station 망포역 = new Station(4L, "망포역");
    private final Station 강남역2 = new Station(5L, "강남역");

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
    @DisplayName("Station이 둘 다 포함되거나 둘 다 미포함되었는지 테스트")
    public void equalContainStatusTest() {
        assertThat(stations.equalContainStatus(강남역.getId(), 광교역.getId())).isTrue();
        assertThat(stations.equalContainStatus(역삼역.getId(), 망포역.getId())).isTrue();
        assertThat(stations.equalContainStatus(강남역.getId(), 망포역.getId())).isFalse();
        assertThat(stations.equalContainStatus(역삼역.getId(), 광교역.getId())).isFalse();
    }

    @Test
    @DisplayName("Station 이름 중복 테스트")
    public void duplicateNameExceptionTest() {
        assertThatThrownBy(() -> new Stations(Arrays.asList(강남역, 강남역2))).isInstanceOf(DuplicateStationNameException.class);
    }
}
