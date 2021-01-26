package subway.station.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> rowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public Station save(String stationName) {
        String SQL = "INSERT INTO station (name) VALUES (?)";
        String SELECT_SQL = "SELECT * FROM station where name = ?";
        jdbcTemplate.update(SQL, stationName);

        return jdbcTemplate.queryForObject(SELECT_SQL, rowMapper, stationName);
    }

    public List<Station> findAll() {
        String SQL = "SELECT * FROM station";
        return jdbcTemplate.query(SQL, rowMapper);
    }

    public void deleteById(Long id) {
        String SQL = "DELETE FROM station where id = ?";
        jdbcTemplate.update(SQL, id);
    }

    public Station findById(Long id) {
        String SELECT_SQL = "SELECT * FROM station where id = ?";
        return jdbcTemplate.queryForObject(SELECT_SQL, rowMapper, id);
    }

}
