package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;

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

        jdbcTemplate.update(con -> {
            PreparedStatement st = con.prepareStatement(sql, new String[]{"id"});
            st.setString(1, line.getName());
            st.setString(2, line.getColor());
            return st;
        }, keyHolder);

        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findOne(long lineID) {
        return jdbcTemplate.queryForObject("select * from line where id = ?", lineRowMapper, lineID);
    }

    public void update(Line line) {
        String sql = "update line set name = ?, color = ? where id = ?";
        int affectedRows = jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getID());

        if (isNotUpdated(affectedRows)) {
            throw new IllegalArgumentException("Could not update line with id: " + line.getID());
        }
    }

    public void delete(long lineID) {
        int affectedRows = jdbcTemplate.update("delete from line where id = ?", lineID);

        if (isNotDeleted(affectedRows)) {
            throw new NoSuchElementException("Could not delete Line with id: " + lineID);
        }
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
