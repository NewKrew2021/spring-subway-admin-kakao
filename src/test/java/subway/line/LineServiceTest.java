package subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import subway.station.Station;
import subway.station.StationService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class LineServiceTest {
    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    private Station station1;
    private Station station2;
    private Station station3;

    @BeforeEach
    void setup() {
        station1 = stationService.createStation(new Station("강남역"));
        station2 = stationService.createStation(new Station("역삼역"));
        station3 = stationService.createStation(new Station("교대역"));
        Line line = new Line("2호선", "green", station1.getId(), station2.getId());
        lineService.createLineAndSection(line, 10);
    }

    @DisplayName("새로운 노선을 생성한다.")
    @Test
    void createLine_success() {
        // given
        Line line = new Line("3호선", "orange", station2.getId(), station3.getId());

        // when
        lineService.createLineAndSection(line, 10);

        // then
        assertThat(lineService.existName("3호선")).isTrue();
    }

    @DisplayName("구간 생성이 실패했을 때 노선 생성까지 롤백된다.")
    @Test
    void createLine_rollbackTest() {
        // given
        Line line = new Line("3호선", "orange", station2.getId(), 1000L);

        // when
        try {
            lineService.createLineAndSection(line, 10);
        } catch (RuntimeException e) {
        }

        // then
        assertThat(lineService.existName("3호선")).isFalse();
    }
}
