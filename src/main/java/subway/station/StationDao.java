package subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Station station) {
        String SQL = "INSERT INTO station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL, new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
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
        String SQL = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(SQL, id);
    }

    public Station findById(Long id) {
        String SQL = "SELECT * FROM station WHERE id = ?";
        return jdbcTemplate.queryForObject(SQL,
                (resultSet, rowNum) ->
                        new Station(resultSet.getLong("id"), resultSet.getString("name")),
                id);
    }

}
