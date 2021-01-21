package subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exceptions.lineExceptions.LineNotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final static RowMapper<Line> lineMapper = (rs, rowNum) ->
            new Line(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color"));

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(LineRequest lineRequest) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into line (name, color) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            psmt.setString(1, lineRequest.getName());
            psmt.setString(2, lineRequest.getColor());
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();
        return new Line(id, lineRequest.getName(), lineRequest.getColor());
    }

    public Line findByName(String name) {
        try {
            return jdbcTemplate.queryForObject("select * from line where name = ?", lineMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void update(Line line) {
        jdbcTemplate.update("update line set name = ?, color = ? where id = ?"
                , line.getName()
                , line.getColor()
                , line.getId());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from line where id = ?", id);
    }

    public List<Line> findAll() {
        return jdbcTemplate.query("select * from line", lineMapper);
    }

    public Line findById(Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from line where id = ?", lineMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new LineNotFoundException();
        }
    }

}
