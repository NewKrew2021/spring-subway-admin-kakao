package subway.domain.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @DisplayName("Station 포함 여부 테스트")
    @ParameterizedTest
    @CsvSource(value = {"1,true", "3,true", "2,false", "4,false"})
    public void containTest(Long id, boolean result) {
        assertThat(stations.contain(id)).isEqualTo(result);
    }

    @DisplayName("Station이 둘 다 포함되거나 둘 다 미포함되었는지 테스트")
    @ParameterizedTest
    @CsvSource(value = {"1,3,true", "2,4,true", "1,4,false", "2,3,false"})
    public void equalContainStatusTest(Long upStationId, Long downStationId, boolean result) {
        assertThat(stations.equalContainStatus(upStationId, downStationId)).isEqualTo(result);
    }

    @Test
    @DisplayName("Station 이름 중복 테스트")
    public void duplicateNameExceptionTest() {
        assertThatThrownBy(() -> new Stations(Arrays.asList(강남역, 강남역2))).isInstanceOf(DuplicateStationNameException.class);
    }
}
