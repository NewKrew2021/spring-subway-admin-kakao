package subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.domain.Station;
import subway.request.LineRequest;
import subway.request.SectionRequest;
import subway.request.StationRequest;
import subway.utils.TableRefresher;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 서비스 관련 기능")
@SpringBootTest
public class LineServiceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationService stationService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private LineService lineService;

    @BeforeEach
    public void setUp() {
        TableRefresher.refreshTables(jdbcTemplate);
        stationService.createStation(new StationRequest("강남역"));
        stationService.createStation(new StationRequest("역삼역"));
        stationService.createStation(new StationRequest("광교역"));
        stationService.createStation(new StationRequest("판교역"));
        lineService.createLine(new LineRequest("분당선", "노랑", 1L, 2L, 3));
        sectionService.addSectionToLine(new SectionRequest(1L, 2L, 3L, 3));
    }

    @DisplayName("지하철 노선 삭제시, 노선에 포함된 지하철 구간도 삭제.")
    @Test
    public void deleteLineTest() {
        assertThat(sectionDao.getByLineId(1L)).isEqualTo(Arrays.asList(
                new Section(1L, 1L, 2L, 3),
                new Section(1L, 2L, 3L, 3)));

        lineService.deleteLine(1L);
        assertThat(sectionDao.getByLineId(1L)).isEqualTo(Collections.emptyList());
    }

    @DisplayName("지하철 노선들을 기반으로 지하철역을 상행부터 하행 순으로 정렬한다.")
    @Test
    public void getOrderedStationsOfLineTest() {
        assertThat(lineService.getOrderedStationsOfLine(1L).getOrderedStations()).isEqualTo(Arrays.asList(
                new Station("강남역"), new Station("역삼역"), new Station("광교역")
        ));
        sectionService.addSectionToLine(new SectionRequest(1L, 4L, 1L, 3));
        assertThat(lineService.getOrderedStationsOfLine(1L).getOrderedStations()).isEqualTo(Arrays.asList(
                new Station("판교역"), new Station("강남역"),
                new Station("역삼역"), new Station("광교역")
        ));
    }
}
