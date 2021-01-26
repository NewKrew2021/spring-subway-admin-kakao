package subway.station.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String SQL = "INSERT INTO station (name) VALUES (?)";
        String SELECT_SQL = "SELECT * FROM station where name = ?";
        jdbcTemplate.update(SQL, station.getName());

        return jdbcTemplate.queryForObject(SELECT_SQL,
                (resultSet, rowNum) -> new Station(resultSet.getLong("id"), resultSet.getString("name")),
                station.getName());
    }

    public List<Station> findAll() {
        String SQL = "SELECT * FROM station";
        return jdbcTemplate.query(
                SQL,
                (resultSet, rowNum) -> new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                )
        );
    }

    public void deleteById(Long id) {
        String SQL = "DELETE FROM station where id = ?";
        jdbcTemplate.update(SQL, id);
    }

    public Station findById(Long id) {
        String SELECT_SQL = "SELECT * FROM station where id = ?";
        return jdbcTemplate.queryForObject(SELECT_SQL,
                (resultSet, rowNum) -> new Station(resultSet.getLong("id"), resultSet.getString("name")),
                id);
    }

}
