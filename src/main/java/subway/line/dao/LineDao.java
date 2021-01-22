package subway.line.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;
import subway.line.dto.LineRequest;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line insert(String name, String color) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement st = con.prepareStatement(LineDaoQuery.INSERT, new String[]{"id"});
            st.setString(1, name);
            st.setString(2, color);
            return st;
        }, keyHolder);

        return new Line(keyHolder.getKey().longValue(), name, color);
    }


    public boolean update(Long id, LineRequest lineRequest) {
        return jdbcTemplate.update(LineDaoQuery.UPDATE, lineRequest.getName(), lineRequest.getColor(), id) > 0;
    }

    public boolean delete(Long id) {
        return jdbcTemplate.update(LineDaoQuery.DELETE, id) > 0;
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(LineDaoQuery.FIND_ALL, lineRowMapper);
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(LineDaoQuery.FIND_BY_ID, lineRowMapper, id);
    }

    public int countByName(String name) {
        return jdbcTemplate.queryForObject(LineDaoQuery.COUNT_BY_NAME, int.class, name);
    }

    private final RowMapper<Line> lineRowMapper =
            (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));
}
