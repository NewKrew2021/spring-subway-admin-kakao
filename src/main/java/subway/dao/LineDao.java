package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.line.Line;

import java.util.List;

@Repository
public class LineDao {
    private final String LINE_SELECT_SQL = "select id, name, color from line ";
    private final String LINE_SELECT_BY_ID_SQL = LINE_SELECT_SQL + "where id = ?";
    private final String LINE_UPDATE_SQL = "update line set name = ?, color = ? where id = ?";
    private final String LINE_DELETE_SQL = "delete from line where id = ?";
    private final String LINE_SELECT_ID_COUNT_SQL = "select count(id) from line where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;
    private final RowMapper<Line> lineMapper = (rs, rowNum) ->
            new Line(rs.getLong(1), rs.getString(2), rs.getString(3));

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        return this.jdbcTemplate.query(LINE_SELECT_SQL, lineMapper);
    }

    public Line getById(Long id) {
        return this.jdbcTemplate.queryForObject(LINE_SELECT_BY_ID_SQL, lineMapper, id);
    }

    public void update(Long id, Line line) {
        this.jdbcTemplate.update(LINE_UPDATE_SQL, line.getName(), line.getColor(), id);
    }

    public boolean deleteById(Long id) {
        return this.jdbcTemplate.update(LINE_DELETE_SQL, id) > 0;
    }

    public boolean contain(Long id) {
        return this.jdbcTemplate.queryForObject(LINE_SELECT_ID_COUNT_SQL, Integer.class, id) > 0;
    }
}
