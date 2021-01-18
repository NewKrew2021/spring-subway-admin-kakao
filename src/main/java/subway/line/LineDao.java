package subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
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

    public void update(Line line) {
        int update = jdbcTemplate.update("update line set name = ?, color = ? where id = ?", line.getName(), line.getColor(), line.getId());
        if (update == 0) {
            throw new IncorrectUpdateSemanticsDataAccessException("일치하는 노선이 없습니다");
        }
    }

    public List<Line> findAll() {
        return jdbcTemplate.query("select * from line", new LineMapper());
    }

    public Optional<Line> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from line where id = ?", new LineMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existBy(String name) {
        return jdbcTemplate.queryForObject("select count(*) from line where name = ?", int.class, name) != 0;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from line where id = ?", id);
    }

    private final static class LineMapper implements RowMapper<Line> {

        @Override
        public Line mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );
        }
    }
}
