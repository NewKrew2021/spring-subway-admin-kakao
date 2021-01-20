package subway.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.domain.Station;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class StationDao {
    private static final String SAVE_SQL = "insert into STATION (name) values (?)";
    private static final String FIND_ALL_SQL = "select id, name from STATION";
    private static final String FIND_BY_ID_SQL = "select id, name from STATION where id = ?";
    private static final String DELETE_SQL = "delete from STATION where id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final StationMapper stationMapper;

    public StationDao(JdbcTemplate jdbcTemplate, StationMapper stationMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationMapper = stationMapper;
    }

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
