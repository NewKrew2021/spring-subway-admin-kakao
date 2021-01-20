package subway.station;

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

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(
                    "insert into STATION(name) values(?)",
                    new String[] {"id"}
            );
            pstmt.setString(1, station.getName());
            return pstmt;
        }, keyHolder);
        return jdbcTemplate.queryForObject(
                "select id, name from STATION where id = ?",
                stationRowMapper, keyHolder.getKey().longValue()
        );
    }

    public int countByName(String stationName) {
        return jdbcTemplate.queryForObject(
                "select count(*) from STATION where name = ?",
                Integer.class, stationName
        );
    }

    public List<Station> findAll() {
        return jdbcTemplate.query("select * from STATION limit 20", stationRowMapper);
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("delete from STATION where id = ?", id);
    }

    public Station findById(long id) {
        return jdbcTemplate.queryForObject("select * from STATION where id = ?", stationRowMapper, id);
    }
}
