package subway.station;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station insert(Station station) {
        String sql = "insert into station (name) values(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement st = con.prepareStatement(sql, new String[]{"id"});
                st.setString(1, station.getName());
                return st;
            }, keyHolder);
        } catch (DataAccessException ignored) {
            throw new IllegalArgumentException(String.format("Station with name %s already exists", station.getName()));
        }

        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Station findByID(Long id) {
        String sql = "select id, name from station where id = ?";
        Station station;

        station = jdbcTemplate.queryForObject(sql, stationRowMapper, id);
        if (stationDoesNotExist(station)) {
            throw new NoSuchElementException(String.format("Could not find station with id: %d", id));
        }

        return station;
    }

    public void deleteByID(Long id) {
        String sql = "delete from station where id = ?";
        int affectedRows = jdbcTemplate.update(sql, id);

        if (affectedRows != 1) {
            throw new NoSuchElementException(String.format("Could not delete station %d", id));
        }
    }

    private boolean stationDoesNotExist(Station station) {
        return station == null;
    }

    private final RowMapper<Station> stationRowMapper =
            (resultSet, rowNum) -> new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name"));
}
