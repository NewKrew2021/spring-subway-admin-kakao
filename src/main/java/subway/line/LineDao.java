package subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LineDao {
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId());
    }

    public List<Line> findAll() {
        String sql = "select id, name, color, start_station_id, end_station_id from line";
        return jdbcTemplate.query(sql, actorRowMapper);
    }

    public Line findById(Long id) {
        String sql = "select id, name, color, start_station_id, end_station_id from line where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, actorRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int countByName(String name) {
        String sql = "select count(*) from line where name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }

    public int deleteById(Long id) {
        String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int updateById(Long id, Line line) {
        String sql = "update line set name = ?, color = ?, start_station_id = ?, end_station_id = ? where id = ?";
        return jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId(), id);
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
