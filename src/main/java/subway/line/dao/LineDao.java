package subway.line.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Line> actorRowMapper = (resultSet, rowNum) ->
            Line.of(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            );

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(LineSql.INSERT.getSql(), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return Line.of(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(LineSql.SELECT.getSql(), actorRowMapper);
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(LineSql.SELECT_BY_ID.getSql(), actorRowMapper, id);
    }

    public Integer existByName(String name) {
        return jdbcTemplate.queryForObject(LineSql.SELECT_COUNT_BY_NAME.getSql(), Integer.class, name);
    }

    public void update(Line newLine) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(LineSql.UPDATE.getSql(), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newLine.getName());
            ps.setString(2, newLine.getColor());
            ps.setLong(3, newLine.getId());
            return ps;
        }, keyHolder);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(LineSql.DELETE.getSql(), id);
    }
}
