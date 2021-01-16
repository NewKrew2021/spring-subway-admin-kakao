package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> actorRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public Station save(Station station) {
        String sql = "insert into station (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        Station persistStation = new Station(keyHolder.getKey().longValue(), station.getName());

        return persistStation;
    }

    public List<Station> findAll() {
        String sql = "select id, name from station";
        return jdbcTemplate.query(sql, actorRowMapper);
    }

    public void deleteById(Long id) {
        String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Station findById(Long id) {
        String sql = "select id, name from station where id = ?";
        return jdbcTemplate.queryForObject(sql, actorRowMapper, id);
    }
}
