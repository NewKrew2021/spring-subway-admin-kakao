package subway.line;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line insert(Line line) {
        String sql = "insert into line (name, color) values(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement st = con.prepareStatement(sql, new String[]{"id"});
                st.setString(1, line.getName());
                st.setString(2, line.getColor());
                return st;
            }, keyHolder);
        } catch (DataAccessException ignored) {
            throw new IllegalArgumentException(String.format("Line with name %s already exists", line.getName()));
        }

        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findOne(Long id) {
        String sql = "select * from line where id = ?";
        Line line = jdbcTemplate.queryForObject(sql, lineRowMapper, id);

        if (!lineExists(line)) {
            throw new NoSuchElementException(String.format("Could not find line with id: %d", id));
        }

        return line;
    }

    public Line update(Long id, LineRequest lineRequest) {
        String sql = "update line set name = ?, color = ? where id = ?";
        int affectedRows = jdbcTemplate.update(sql, lineRequest.getName(), lineRequest.getColor(), id);

        if (!affectedToUniqueRowOnly(affectedRows)) {
            throw new NoSuchElementException(String.format("Could not update line with id: %d", id));
        }

        return findOne(id);
    }

    public void delete(Long id) {
        String sql = "delete from line where id = ?";
        int affectedRows = jdbcTemplate.update(sql, id);

        if (!affectedToUniqueRowOnly(affectedRows)) {
            throw new NoSuchElementException(String.format("Could not delete line with id: %d", id));
        }
    }

    private boolean lineExists(Line line) {
        return line != null;
    }

    private boolean affectedToUniqueRowOnly(int affectedRows) {
        return affectedRows == 1;
    }

    private final RowMapper<Line> lineRowMapper =
            (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));
}
