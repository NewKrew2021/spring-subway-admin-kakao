package subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class StationDao {
    public static final String SAVE_SQL = "insert into STATION (name) values (?)";
    public static final String FIND_ALL_SQL = "select id, name from STATION";
    public static final String FIND_BY_ID_SQL = "select id, name from STATION where id = ?";
    public static final String DELETE_SQL = "delete from STATION where id = ?";

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    StationMapper stationMapper;

    public Long save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, stationMapper);
    }

    public Station findById(Long stationId) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, stationMapper, stationId);
    }

    public void deleteById(Long stationId) {
        jdbcTemplate.update(DELETE_SQL, stationId);
    }
}
