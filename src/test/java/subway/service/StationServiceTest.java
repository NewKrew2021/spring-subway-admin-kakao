package subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.request.LineRequest;
import subway.request.SectionRequest;
import subway.request.StationRequest;
import subway.utils.TableRefresher;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("지하철역 서비스 관련 기능")
@SpringBootTest
public class StationServiceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StationService stationService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private LineService lineService;

    @BeforeEach
    public void setUp() {
        TableRefresher.refreshTables(jdbcTemplate);
    }

    @DisplayName("지하철 구간에서 사용 중인 지하철역을 제거한다.")
    @Test
    public void deleteStationTest() {
        stationService.createStation(new StationRequest("강남역"));
        stationService.createStation(new StationRequest("역삼역"));
        stationService.createStation(new StationRequest("광교역"));

        lineService.createLine(new LineRequest("분당선", "노랑", 1L, 2L, 3));
        sectionService.addSectionToLine(new SectionRequest(1L, 2L, 3L, 3));
        assertThatThrownBy(() -> stationService.deleteStation(2L)).isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> stationService.deleteStation(3L)).isInstanceOf(DataAccessException.class);

        sectionService.deleteStationFromLine(1L, 2L);
        assertThat(stationService.deleteStation(2L)).isTrue();
    }
}
