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

    public Line insert(String name, String color) {
        validateName(name);

        String sql = "insert into line (name, color) values(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                PreparedStatement st = con.prepareStatement(sql, new String[]{"id"});
                st.setString(1, name);
                st.setString(2, color);
                return st;
            }, keyHolder);

        return new Line(keyHolder.getKey().longValue(), name, color);
    }


    public boolean update(Long id, LineRequest lineRequest) {
        String sql = "update line set name = ?, color = ? where id = ?";
        return jdbcTemplate.update(sql, lineRequest.getName(), lineRequest.getColor(), id) > 0;
    }

    public boolean delete(Long id) {
        String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        String sql = "select * from line where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public void validateName(String name) {
        String sql = "select count(*) from line where name = ?";
        if (jdbcTemplate.queryForObject(sql, int.class, name) != 0) {
            throw new IllegalArgumentException("이미 등록된 노선 입니다.");
        }
    }

    private final RowMapper<Line> lineRowMapper =
            (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));
}
