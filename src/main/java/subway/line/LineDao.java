package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LineDao {
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Line> actorRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return line;
    };

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        String sql = "insert into line (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        Line persistLine = new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());

        return persistLine;
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from line";
        return jdbcTemplate.query(sql, actorRowMapper);
    }

    public Line findById(Long id) {
        String sql = "select id, name, color from line where id = ?";
        return jdbcTemplate.queryForObject(sql, actorRowMapper, id);
    }

    public Line findByName(String name) {
        String sql = "select id, name, color from line where name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, actorRowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void update(Line originLine, Line updateLine) {
        String sql = "update line set name=?, color=? where id = ?";
        jdbcTemplate.update(sql, updateLine.getName(), updateLine.getColor() ,originLine.getId());
    }

    public void deleteById(Long id) {
        String sql = "delete from line where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
