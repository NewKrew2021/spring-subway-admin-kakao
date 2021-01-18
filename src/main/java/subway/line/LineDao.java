package subway.line;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return line;
    };

    public Line insert(Line line) {
        String sql = "insert into line (name, color) values(?, ?)";

        if (findByName(line.getName()) != null) {
            return null;
        }

        long id = jdbcTemplate.update(con -> {
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, line.getName());
            st.setString(2, line.getColor());
            return st;
        }, new GeneratedKeyHolder());

        return new Line(id, line.getName(), line.getColor());
    }

    public Line findById(Long id) {
        String sql = "select * from line where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public Line findByName(String name) {
        String sql = "select * from line where name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, lineRowMapper, name);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public boolean update(Long id, LineRequest lineRequest) {
        String sql = "update line set name = ?, color = ? where id = ?";
        return jdbcTemplate.update(sql, lineRequest.getName(), lineRequest.getColor(), id) > 0;
    }

    public boolean deleteById(Long id) {
        String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
}
