package subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        String sql = "insert into LINE (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return Line.of(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from LINE";
        return jdbcTemplate.query(sql, actorRowMapper);
    }

    public Line findById(Long id) {
        String sql = "select id, name, color from LINE where id = ?";
        return jdbcTemplate.queryForObject(sql, actorRowMapper, id);
    }

    public Line findByName(String name) {
        String sql = "select id, name, color from LINE where name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, actorRowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void update(Line newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newLine.getName());
            ps.setString(2, newLine.getColor());
            ps.setLong(3, newLine.getId());
            return ps;
        }, keyHolder);
    }

    public void deleteById(Long id) {
        String sql = "delete from LINE where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
