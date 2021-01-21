package subway.station.dao;

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

    public Station insert(String name) {
        String sql = "insert into station (name) values(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement st = con.prepareStatement(sql, new String[]{"id"});
            st.setString(1, name);
            return st;
        }, keyHolder);

        return new Station(keyHolder.getKey().longValue(), name);
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

    public int countByName(String name) {
        String sql = "select count(*) from station where name = ?";
        return jdbcTemplate.queryForObject(sql, int.class, name);
    }

    private final RowMapper<Station> stationRowMapper =
            (resultSet, rowNum) -> new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name"));
}
