package subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class StationDaoTest {

    @Autowired
    private StationDao stationDao;
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
    @DisplayName("id값으로 db내에 존재하는 지하철 역을 찾는다.")
    public void findById_ifExist() {
        Station newStation = stationDao.save(new Station("오이도역"));
        assertThat(stationDao.findById(newStation.getId()).getId()).isEqualTo(newStation.getId());
    }

    @Test
    @DisplayName("id값으로 db내에 존재하지 않는 지하철 역을 찾는다.")
    public void findById_ifNotExist() {
        assertThat(stationDao.findById(1L)).isEqualTo(null);
    }

    @Test
    @DisplayName("전체 지하철 역을 찾는다.")
    public void findAll() {
        stationDao.save(new Station("오이도"));
        stationDao.save(new Station("정왕"));
        stationDao.save(new Station("신길온천"));
        stationDao.save(new Station("안산"));
        assertThat(stationDao.findAll().size()).isEqualTo(4);
    }

}

