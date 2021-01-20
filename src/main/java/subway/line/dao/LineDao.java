package subway.line.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;

import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final LineMapper lineMapper;

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate, LineMapper lineMapper){
        this.jdbcTemplate = jdbcTemplate;
        this.lineMapper = lineMapper;
    }

    public void save(Line line) {
        jdbcTemplate.update(LineQuery.SAVE.getQuery(),
                line.getName(), line.getColor(), line.getUpStationId(),
                line.getDownStationId(), line.getDistance());
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(LineQuery.FIND_ALL.getQuery(), lineMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(LineQuery.DELETE_BY_ID.getQuery(), id);
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(LineQuery.FIND_BY_ID.getQuery(), lineMapper, id);
    }

    public Line findByName(String name) {
        return jdbcTemplate.queryForObject(LineQuery.FIND_BY_NAME.getQuery(), lineMapper, name);
    }

    public void update(Line line) {
        jdbcTemplate.update(LineQuery.UPDATE.getQuery(),
                line.getUpStationId(), line.getDownStationId(),
                line.getDistance(), line.getId());
    }
}
