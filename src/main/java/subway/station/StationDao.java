package subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("station")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();

        return new Station(id,station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return rows.stream()
                .map(row -> new Station((Long)row.get("id"), row.get("name").toString()))
                .collect(Collectors.toList());
    }

    public Station findOne(Long stationId){
        String sql = "select * from STATION where id = ?";
        return jdbcTemplate.queryForObject(sql, (resultSet, idx) -> new Station(resultSet.getLong("id"), resultSet.getString("name")), stationId);
    }

    public int deleteById(Long stationId) {
        String sql = "delete from STATION where id = ?";
        return jdbcTemplate.update(sql, stationId);
    }

}
