package subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.station.Station;
import subway.station.StationDao;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @Test
    public void saveTest() {
        assertThat(stationDao.save(강남역)).isEqualTo(강남역);
        assertThatThrownBy(() -> stationDao.save(강남역)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void getByIdTest() {
        stationDao.save(강남역);
        assertThat(stationDao.getById(1L)).isEqualTo(강남역);
    }

    @Test
    public void findAll() {
        stationDao.save(강남역);
        stationDao.save(역삼역);
        assertThat(stationDao.findAll()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역));
    }

    @Test
    public void deleteByIdTest() {
        stationDao.save(강남역);
        assertThat(stationDao.deleteById(1L)).isTrue();
        assertThat(stationDao.deleteById(1L)).isFalse();
    }
}
