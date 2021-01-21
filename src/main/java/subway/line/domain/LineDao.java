package subway.line.domain;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.exceptions.lineExceptions.LineNotFoundException;
import subway.line.domain.Line;

import java.util.List;

@Repository
public class LineDao {

    private static final String SELECT_FROM_LINE_WHERE_NAME = "select * from line where name = ?";
    private static final String UPDATE_LINE_SET_NAME_COLOR_WHERE_ID = "update line set name = ?, color = ? where id = ?";
    private static final String DELETE_FROM_LINE_WHERE_ID = "delete from line where id = ?";
    private static final String SELECT_FROM_LINE = "select * from line";
    private static final String SELECT_FROM_LINE_WHERE_ID = "select * from line where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final static RowMapper<Line> lineMapper = (rs, rowNum) ->
            new Line(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color"),
                    rs.getInt("extra_fare"));

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor())
                .addValue("extra_fare", line.getExtraFare());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        return new Line(id.longValue(), line.getName(), line.getColor(), line.getExtraFare());
    }

    public void update(Line line) {
        jdbcTemplate.update(UPDATE_LINE_SET_NAME_COLOR_WHERE_ID
                , line.getName()
                , line.getColor()
                , line.getId());
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(SELECT_FROM_LINE, lineMapper);
    }

    public Line findByName(String name) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FROM_LINE_WHERE_NAME, lineMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Line findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FROM_LINE_WHERE_ID, lineMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new LineNotFoundException();
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_FROM_LINE_WHERE_ID, id);
    }

}
