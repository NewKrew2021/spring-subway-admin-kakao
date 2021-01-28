package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
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

    public boolean isExistLine(Long id) {
        String sql = "select count(*) from LINE where id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id) != 0;
    }

    public boolean isDuplicateName(String name) {
        String sql = "select count(*) from LINE where name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, name) != 0;
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

    public int deleteById(Long id) {
        String sql = "delete from LINE where id = ?";

        return jdbcTemplate.update(sql, id);
    }
}
