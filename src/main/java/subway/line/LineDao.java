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
        jdbcTemplate.update(LineQuery.SAVE.getQuery(),
                line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId(), line.getDistance());
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(LineQuery.FIND_ALL.getQuery(), lineMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(LineQuery.DELETE_BY_ID.getQuery(),id);
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(LineQuery.FIND_BY_ID.getQuery(),lineMapper, id);
    }

    public Line findByName(String name) {
        return jdbcTemplate.queryForObject(LineQuery.FIND_BY_NAME.getQuery(),lineMapper, name);
    }

    public void update(Line line) {
        jdbcTemplate.update(LineQuery.UPDATE.getQuery(),
                line.getUpStationId(), line.getDownStationId(), line.getDistance(), line.getId());
    }
}
