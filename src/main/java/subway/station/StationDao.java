package subway.station;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    public Station insert(Station station) {
        Stations stations = new Stations(findAll());

        if (stations.hasDuplicate(station)) {
            return null;
        }

        String sql = "insert into station (name) values(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement st = con.prepareStatement(sql, new String[]{"id"});
                st.setString(1, station.getName());
                return st;
            }, keyHolder);
        } catch (DataAccessException ignored) {
            return null;
        }

        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Station findById(Long id) {
        String sql = "select id, name from station where id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }

    public boolean deleteById(Long id) {
        String sql = "delete from station where id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    private final RowMapper<Station> stationRowMapper =
            (resultSet, rowNum) -> new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name"));
}
