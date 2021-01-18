package subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exception.DuplicateException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static subway.line.LineQuery.*;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        if (hasDuplicateName(line.getName())) {
            throw new DuplicateException();
        }
        return insertAtDB(line);
    }

    public void update(Line line) {
        jdbcTemplate.update(updateQuery, line.getName(), line.getColor(), line.getId());
    }

    public Optional<Line> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(selectByIdQuery, new LineMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Line> findAll() {
        List<Line> lines = jdbcTemplate.query(selectAllQuery, new LineMapper());
        return lines;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(deleteByIdQuery, id);
    }

    public boolean hasDuplicateName(String name) {
        return jdbcTemplate.queryForObject(countByNameQuery, int.class, name) != 0;
    }

    private Line insertAtDB(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    insertQuery,
                    Statement.RETURN_GENERATED_KEYS);
            psmt.setString(1, line.getName());
            psmt.setString(2, line.getColor());
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();

        return new Line(
                id,
                line.getName(),
                line.getColor()
        );
    }

    private final static class LineMapper implements RowMapper<Line> {
        @Override
        public Line mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String color = rs.getString("color");

            return new Line(id, name, color);
        }
    }
}
