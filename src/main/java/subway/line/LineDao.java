package subway.line;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;

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
            return null;
        }

        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findOne(Line line) {
        return jdbcTemplate.queryForObject("select * from line where id = ?", lineRowMapper, line.getID());
    }

    public Line update(Line line) {
        String sql = "update line set name = ?, color = ? where id = ?";
        int affectedRows;

        try {
            affectedRows = jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getID());
        } catch (DataAccessException ignored) {
            return null;
        }

        if (isNotUpdated(affectedRows)) {
            return null;
        }

        return line;
    }

    public Line delete(Line line) {
        int affectedRows = jdbcTemplate.update("delete from line where id = ?", line.getID());

        if (isNotDeleted(affectedRows)) {
            return line;
        }

        return null;
    }

    private boolean isNotUpdated(int affectedRows) {
        return noRowsWereAffected(affectedRows);
    }

    private boolean isNotDeleted(int affectedRows) {
        return noRowsWereAffected(affectedRows);
    }

    private boolean noRowsWereAffected(int affectedRows) {
        return affectedRows != 1;
    }

    private final RowMapper<Line> lineRowMapper =
            (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));
}
