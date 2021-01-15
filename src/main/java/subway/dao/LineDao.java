package subway.dao;

import subway.domain.Line;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.query.Sql;

import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineMapper = (rs, rowNum) ->
            new Line(rs.getLong(1), rs.getString(2), rs.getString(3));

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        this.jdbcTemplate.update(Sql.INSERT_LINE, line.getName(), line.getColor());
        return this.jdbcTemplate.queryForObject(Sql.SELECT_LINE_WITH_NAME,
                lineMapper,
                line.getName());
    }

    public List<Line> findAll() {
        return this.jdbcTemplate.query(
                Sql.SELECT_ALL_LINES,
                lineMapper
        );
    }

    public Line getById(Long id) {
        return this.jdbcTemplate.queryForObject(
                Sql.SELECT_LINE_WITH_ID,
                lineMapper,
                id
        );
    }

    public void update(Long id, Line line) {
        this.jdbcTemplate.update(Sql.UPDATE_LINE_WITH_ID,
                line.getName(), line.getColor(), id);
    }

    public boolean deleteById(Long id) {
        return this.jdbcTemplate.update(Sql.DELETE_LINE_BY_ID, id) > 0;
    }
}
