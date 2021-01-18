package subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import subway.request.LineRequest;
import subway.request.SectionRequest;
import subway.request.StationRequest;
import subway.response.LineResponse;
import subway.response.StationResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("지하철역 서비스 관련 기능")
@SpringBootTest
@Sql("classpath:/deleteAll.sql")
public class StationServiceTest {
    @Autowired
    private StationService stationService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private LineService lineService;

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 광교역;

    @DisplayName("지하철 구간에서 사용 중인 지하철역을 제거한다.")
    @Test
    public void deleteStationTest() {
        강남역 = stationService.createStation(new StationRequest("강남역"));
        역삼역 = stationService.createStation(new StationRequest("역삼역"));
        광교역 = stationService.createStation(new StationRequest("광교역"));

        LineResponse 분당선 = lineService.createLine(new LineRequest("분당선", "노랑", 강남역.getId(), 역삼역.getId(), 3));
        sectionService.addSectionToLine(new SectionRequest(분당선.getId(), 역삼역.getId(), 광교역.getId(), 3));
        assertThatThrownBy(() -> stationService.deleteStation(역삼역.getId())).isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> stationService.deleteStation(광교역.getId())).isInstanceOf(DataAccessException.class);

        sectionService.deleteStationFromLine(분당선.getId(), 역삼역.getId());
        assertThat(stationService.deleteStation(역삼역.getId())).isTrue();
    }
}
