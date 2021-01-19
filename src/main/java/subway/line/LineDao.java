package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import subway.exceptions.DuplicateException;

import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return line;
    };

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        if(isExist(line)) {
            throw new DuplicateException("동일한 이름을 가지는 line이 이미 존재합니다.");
        }
        jdbcTemplate.update(LineQuery.insert, line.getName(), line.getColor());
        return jdbcTemplate.queryForObject(LineQuery.selectIdNameColorByName, lineRowMapper, line.getName());
    }

    private boolean isExist(Line line){
        return 0 < jdbcTemplate.queryForObject(LineQuery.countByName, int.class, line.getName());
    }

    public void update(Line line) {
        jdbcTemplate.update(LineQuery.updateNameAndColorById, line.getName(), line.getColor(), line.getId());
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(LineQuery.selectIdNameColorById, lineRowMapper, id);
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(LineQuery.selectIdNameColorOfAll, lineRowMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(LineQuery.deleteById, id);
    }
}
