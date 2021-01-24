package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.exception.AlreadyExistDataException;
import subway.exception.DataEmptyException;
import subway.exception.DeleteImpossibleException;
import subway.exception.UpdateImpossibleException;

import java.util.List;

@Repository
public class LineDao {
    public static final String DELETE_FROM_LINE_WHERE_ID = "delete from LINE where id = ?";
    public static final String SELECT_ID_FROM_LINE = "select id from LINE";
    public static final String SELECT_FROM_LINE_WHERE_ID = "select * from LINE where id = ?";
    public static final String UPDATE_LINE_SET_COLOR_NAME_WHERE_ID = "update LINE set color = ?, name = ? where id = ?";
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("line")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        try {
            Long lineId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            return new Line(lineId, line.getName(), line.getColor());
        } catch (RuntimeException e) {
            throw new AlreadyExistDataException();
        }

    }

    public void deleteById(Long lineId) {
        String sql = DELETE_FROM_LINE_WHERE_ID;
        if (jdbcTemplate.update(sql, lineId) == 0) {
            throw new DeleteImpossibleException();
        }
    }

    public List<Line> findAll() {
        String sql = SELECT_ID_FROM_LINE;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));
    }

    public Line findOne(Long lineId) {
        String getLineSql = SELECT_FROM_LINE_WHERE_ID;
        Line line = jdbcTemplate.queryForObject(getLineSql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")), lineId);
        if (line == null) {
            throw new DataEmptyException();
        }
        return new Line(line.getId(), line.getName(), line.getColor());
    }

    public void update(Line line) {
        String sql = UPDATE_LINE_SET_COLOR_NAME_WHERE_ID;
        if (jdbcTemplate.update(sql, line.getColor(), line.getName(), line.getId()) == 0) {
            throw new UpdateImpossibleException();
        }
    }
}
