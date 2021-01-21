package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.domain.Station;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sql = "insert into station (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, station.getName());
            return pstmt;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(),
                station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ));
    }

    public Station findOne(Long id) {
        String sql = "select id, name from station where id = ?";
        return jdbcTemplate.queryForObject(
                sql,
                (resultSet, rowNum) -> new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ), id);
    }

    public void deleteById(Long id) {
        String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        String sql = "delete from station";
        jdbcTemplate.update(sql);
    }
}
