package subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.BooleanSupplier;

@Repository
public class StationDao {
    private JdbcTemplate jdbcTemplate;

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
        if (checkExistName(station.getName())) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }
        String sql = "insert into station (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return new Station(keyHolder.getKey().longValue(), station.getName());

    }

    public boolean checkExistName(String name) {
        String sql = "select exists(select * from station where name=?) as success";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public List<Station> findAll() {
        String sql = "select id, name from station limit 50";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Station findStationById(Long id) {
        String sql = "select id, name from station where id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }

    public List<Station> findStationByName(String name) {
        String sql = "select id, name from station where name = ?";
        return jdbcTemplate.query(sql, stationRowMapper, name);
    }

    public void deleteStationById(Long id) {
        String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql, Long.valueOf(id));
    }
}

