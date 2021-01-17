package subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import subway.DuplicateException;

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
        if (hasDuplicateName(line.getName())) {
            throw new DuplicateException("동일한 이름을 가지는 line이 이미 존재합니다.");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into line (name, color) values(?, ?)",
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

    public void update(Line line) {
        String query = "update line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(query, line.getName(), line.getColor(), line.getId());
    }

    public Optional<Line> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from line where id = ?", new LineMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Line> findAll() {
        String sqlQuery = "select * from line";
        List<Line> lines = jdbcTemplate.query(sqlQuery, new LineMapper());
        return lines;
    }

    public void deleteById(Long id) {
        String sqlQuery = "delete from line where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public boolean hasDuplicateName(String name) {
        String sqlQuery = "select count(*) from line where name = ?";
        int count = jdbcTemplate.queryForObject(sqlQuery, int.class, name);
        return count != 0;
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
