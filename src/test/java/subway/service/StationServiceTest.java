package subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.line.Line;
import subway.domain.station.Station;
import subway.exception.NotExistException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class StationServiceTest {

    @Autowired
    StationService stationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE station IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE station(\n" +
                "    id bigint auto_increment not null,\n" +
                "    name varchar(255) not null unique,\n" +
                "    primary key(id)\n" +
                "    )");
    }

    @Test
    @DisplayName("create와 id값으로 지하철 역 찾기가 잘 되는가")
    public void createAndGetStation() {
        Station newStation = stationService.createStation("오이도역");
        assertThat(stationService.getStation(newStation.getId()).getName()).isEqualTo("오이도역");
    }

    @Test
    @DisplayName("모든 지하철역을 가져오는 것이 잘 되는가")
    public void getAllStations() {
        stationService.createStation("오이도역");
        stationService.createStation("정왕역");
        stationService.createStation("신길온천역");
        stationService.createStation("안산역");
        assertThat(stationService.getAllStations().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("지하철역 삭제가 잘 되고, 삭제된 역을 조회했을 때 익셉션이 발생하는가")
    public void deleteStation() {
        //given
        stationService.createStation("오이도역");
        stationService.createStation("정왕역");
        Station target = stationService.createStation("광교역");

        //when
        stationService.deleteStation(target.getId());

        //then
        assertThatThrownBy(()-> stationService.getStation(target.getId()))
                .isInstanceOf(NotExistException.class)
                .hasMessage(StationService.NOT_EXIST_STATION_ERROR_MESSAGE);

    }

}
