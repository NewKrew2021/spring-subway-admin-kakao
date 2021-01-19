package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LineDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    LineMapper lineMapper;

    public void save(Line line) {
        jdbcTemplate.update("insert into LINE (name, color, up_station_id, down_station_id, distance) values (?, ?, ?, ?, ?)",
                line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId(), line.getDistance());
    }

    public List<Line> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, lineMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from LINE where id = ?",id);
    }

    public Line findById(Long id) {
        String sql = "select * from LINE where id = ?";
        return jdbcTemplate.queryForObject(sql,lineMapper, id);
    }

    public Line findByName(String name) {
        String sql = "select * from LINE where name = ?";
        return jdbcTemplate.queryForObject(sql,lineMapper, name);
    }

    public void update(Line line) {
        jdbcTemplate.update("update LINE set up_station_id = ?, down_station_id = ?, distance = ? where id = ?",
                line.getUpStationId(), line.getDownStationId(), line.getDistance(), line.getId());
    }
}
