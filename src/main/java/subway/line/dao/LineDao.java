package subway.line.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
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

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> rowMapper = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );

    public Line save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertLine(line), keyHolder);
        return findById((Long) keyHolder.getKey());
    }

    private PreparedStatementCreator insertLine(Line line) {
        return con -> {
            PreparedStatement psmt = con.prepareStatement("INSERT INTO line(name,color) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            psmt.setString(1, line.getName());
            psmt.setString(2, line.getColor());
            return psmt;
        };
    }


    public List<Line> findAll() {
        String SQL = "SELECT id, name, color FROM line";
        return jdbcTemplate.query(SQL, rowMapper);
    }

    public Line findById(long id) {
        String SQL = "SELECT id, name, color FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(SQL, rowMapper, id);
    }


    public void deleteLineById(long id) {
        String SQL = "DELETE FROM line where id = ?";
        jdbcTemplate.update(SQL, id);
    }

    public int findByName(String name) {
        String SQL = "SELECT count(*) from line where name = ?";
        return jdbcTemplate.queryForObject(SQL, Integer.class, name);
    }

    public int update(Line line) {
        String SQL = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        return jdbcTemplate.update(SQL, line.getName(), line.getColor(), line.getId());
    }
}
