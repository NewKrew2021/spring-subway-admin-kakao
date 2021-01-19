package subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class StationDao {
    public static final String SAVE_SQL = "insert into STATION (name) values (?)";
    public static final String FIND_ALL_SQL = "select id, name from STATION";
    public static final String FIND_BY_ID_SQL = "select id, name from STATION where id = ?";
    public static final String FIND_BY_NAME_SQL = "select id, name from STATION where name = ?";
    public static final String DELETE_SQL = "delete from STATION where id = ?";

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    StationMapper stationMapper;

    public void save(Station station) {
        jdbcTemplate.update(SAVE_SQL, station.getName());
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, stationMapper);
    }

    public Station findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, stationMapper, id);
    }

    public Station findByName(String name) {
        return jdbcTemplate.queryForObject(FIND_BY_NAME_SQL, stationMapper, name);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_SQL, id);
    }

}
