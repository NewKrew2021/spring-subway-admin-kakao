package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class LineDao {
    private static final String SAVE_SQL = "insert into LINE (name, color) values (?, ?)";
    private static final String FIND_ALL_SQL = "select id, name, color from LINE";
    private static final String FIND_BY_ID_SQL = "select id, name, color from LINE where id = ?";
    private static final String UPDATE_SQL = "update LINE set name = ?, color = ? where id = ?";
    private static final String DELETE_SQL = "delete from LINE where id = ?";

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    LineMapper lineMapper;

    public Long save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Line> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, lineMapper);
    }

    public Line findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, lineMapper, id);
    }

    public void update(Line line) {
        jdbcTemplate.update(UPDATE_SQL, line.getName(), line.getColor(), line.getId());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_SQL, id);
    }
}
