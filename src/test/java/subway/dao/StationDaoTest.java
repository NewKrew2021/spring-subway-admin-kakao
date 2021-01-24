package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import subway.domain.station.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DirtiesContext
@SpringBootTest
public class StationDaoTest {
    private final Station 강남역 = new Station("강남역");
    private final Station 역삼역 = new Station("역삼역");

    @Autowired
    StationDao stationDao;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE STATION IF EXISTS");
        jdbcTemplate.execute("create table if not exists STATION\n" +
                "(\n" +
                "    id bigint auto_increment not null,\n" +
                "    name varchar(255) not null unique,\n" +
                "    primary key(id)\n" +
                ");");
    }

    @DisplayName("역을 저장한다.")
    @ParameterizedTest
    @ValueSource(strings = {"강남역", "역삼역"})
    public void saveTest(String name) {
        Station station = new Station(name);

        Station savedStation = stationDao.save(station);

        assertThat(savedStation).isEqualTo(station);
    }

    @DisplayName("중복된 이름의 역을 저장한다.")
    @ParameterizedTest
    @ValueSource(strings = {"강남역", "역삼역"})
    public void saveDuplicatedStation(String name) {
        Station station = new Station(name);

        stationDao.save(station);

        assertThatThrownBy(() -> stationDao.save(station)).isInstanceOf(DataAccessException.class);
    }

    @DisplayName("Station ID로 역을 조회한다.")
    @ValueSource(strings = {"강남역", "역삼역"})
    @ParameterizedTest
    public void getByIdTest(String name) {
        Station station = stationDao.save(new Station(name));

        assertThat(stationDao.getById(station.getId())).isEqualTo(station);
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    public void findAll() {
        stationDao.save(강남역);
        stationDao.save(역삼역);

        assertThat(stationDao.findAll()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역));
    }

    @DisplayName("Station ID로 역을 삭제한다.")
    @ValueSource(strings = {"강남역", "역삼역"})
    @ParameterizedTest
    public void deleteByIdTest(String name) {
        Station station = stationDao.save(new Station(name));

        assertThat(stationDao.deleteById(station.getId())).isTrue();
    }

    @DisplayName("Station ID로 역을 삭제한다.")
    @ValueSource(strings = {"강남역", "역삼역"})
    @ParameterizedTest
    public void failToDeleteByIdTest(String name) {
        Station station = stationDao.save(new Station(name));

        stationDao.deleteById(station.getId());

        assertThat(stationDao.deleteById(station.getId())).isFalse();
    }

}
