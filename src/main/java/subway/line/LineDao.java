package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class LineDao {
    public static final String SAVE_SQL = "insert into LINE (name, color, up_station_id, down_station_id, distance) values (?, ?, ?, ?, ?)";
    public static final String FIND_ALL_SQL = "select id, name, color, up_station_id, down_station_id, distance from LINE";
    public static final String DELETE_SQL = "delete from LINE where id = ?";
    public static final String FIND_BY_ID_SQL = "select id, name, color, up_station_id, down_station_id, distance from LINE where id = ?";
    public static final String FIND_BY_NAME_SQL = "select id, name, color, up_station_id, down_station_id, distance from LINE where name = ?";
    public static final String UPDATE_SQL = "update LINE set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    LineMapper lineMapper;

    public void save(Line line) {
        jdbcTemplate.update(SAVE_SQL, line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId(), line.getDistance());
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, lineMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_SQL, id);
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, lineMapper, id);
    }

    public Line findByName(String name) {
        return jdbcTemplate.queryForObject(FIND_BY_NAME_SQL, lineMapper, name);
    }

    public void update(Line line) {
        jdbcTemplate.update(UPDATE_SQL, line.getUpStationId(), line.getDownStationId(), line.getDistance(), line.getId());
    }
}
