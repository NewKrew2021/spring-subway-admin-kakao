package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.query.Sql;

import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;
    private final RowMapper<Line> lineMapper = (rs, rowNum) ->
            new Line(rs.getLong(1), rs.getString(2), rs.getString(3));

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        Long lineId = insertActor.executeAndReturnKey(parameters).longValue();
        return new Line(lineId, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        return this.jdbcTemplate.query(
                Sql.SELECT_ALL_LINES,
                lineMapper
        );
    }

    public Line getById(Long lineId) {
        return this.jdbcTemplate.queryForObject(
                Sql.SELECT_LINE_WITH_ID,
                lineMapper,
                lineId
        );
    }

    public boolean update(Long lineId, Line line) {
        return this.jdbcTemplate.update(Sql.UPDATE_LINE_WITH_ID,
                line.getName(), line.getColor(), lineId) > 0;
    }

    public boolean deleteById(Long lineId) {
        return this.jdbcTemplate.update(Sql.DELETE_LINE_BY_ID, lineId) > 0;
    }
}
