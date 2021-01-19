package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exceptions.exception.LineDuplicatedException;
import subway.exceptions.exception.LineNothingToUpdateException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final static RowMapper<Line> lineMapper = (rs, rowNum) ->
            new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color"));

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        if (existBy(line.getName())) {
            throw new LineDuplicatedException();
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into line (name, color) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            psmt.setString(1, line.getName());
            psmt.setString(2, line.getColor());
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();
        return new Line(id, line.getName(), line.getColor());
    }

    public boolean existBy(String name) {
        return jdbcTemplate.queryForObject("select count(*) from line where name = ?", int.class, name) != 0;
    }

    public void update(Line line) {
        int update = jdbcTemplate.update("update line set name = ?, color = ? where id = ?",
                line.getName(), line.getColor(), line.getId());
        if (update == 0) {
            throw new LineNothingToUpdateException();
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from line where id = ?", id);
    }

    public List<Line> findAll() {
        return jdbcTemplate.query("select * from line", lineMapper);
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject("select * from line where id = ?", lineMapper, id);
    }

}
