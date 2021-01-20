package subway.line;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line insert(Line line) {
        if (isDuplicatedName(line)) {
            return null;
        }

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

    public Line findOne(Long id) {
        String sql = "select * from line where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public boolean update(Long id, LineRequest lineRequest) {
        String sql = "update line set name = ?, color = ? where id = ?";
        return jdbcTemplate.update(sql, lineRequest.getName(), lineRequest.getColor(), id) > 0;
    }

    public boolean delete(Long id) {
        String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public boolean isDuplicatedName(Line line) {
        return findByName(line.getName()) != null;
    }

    private Line findByName(String name) {
        String sql = "select * from line where name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, lineRowMapper, name);
        } catch (DataAccessException e) {
            return null;
        }
    }

    private final RowMapper<Line> lineRowMapper =
            (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));
}
