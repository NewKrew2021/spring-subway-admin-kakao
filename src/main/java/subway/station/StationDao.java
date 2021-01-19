package subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.exceptions.DuplicateException;

import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sqlQuery = "insert into station(name) values (?)";
        try {
            jdbcTemplate.update(sqlQuery, station.getName());
        } catch (Exception e) {
            throw new DuplicateException("동일한 이름을 가지는 station이 이미 존재합니다.");
        }
        return jdbcTemplate.queryForObject("select id, name from station where name = ?", stationRowMapper, station.getName());
    }

    public List<Station> findAll() {
        String sqlQuery = "select * from station";
        return jdbcTemplate.query(sqlQuery, stationRowMapper);
    }

    public Station findById(Long id) {
        String sqlQuery = "select * from station where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, stationRowMapper, id);
    }

    public void deleteById(Long id) {
        String sqlQuery = "delete from station where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }
}