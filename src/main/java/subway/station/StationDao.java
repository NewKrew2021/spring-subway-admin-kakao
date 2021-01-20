package subway.station;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

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
            return null;
        }

        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    public List<Station> findAll() {
        return jdbcTemplate.query("select * from station", stationRowMapper);
    }

    public Station findByID(Station station) {
        return jdbcTemplate.queryForObject("select id, name from station where id = ?",
                stationRowMapper, station.getID());
    }

    public Station deleteByID(Station station) {
        int affectedRows = jdbcTemplate.update("delete from station where id = ?", station.getID());

        if (isNotDeleted(affectedRows)) {
            return station;
        }

        return null;
    }

    private boolean isNotDeleted(int affectedRows) {
        return affectedRows != 1;
    }

    private final RowMapper<Station> stationRowMapper =
            (resultSet, rowNum) -> new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name"));
}
