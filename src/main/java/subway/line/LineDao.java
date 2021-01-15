package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineMapper = (rs, rowNum) ->
            new Line(rs.getLong(1), rs.getString(2), rs.getString(3));

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        this.jdbcTemplate.update("insert into line (name, color) values (?, ?)", line.getName(), line.getColor());
        return this.jdbcTemplate.queryForObject("select * from line where name = ?",
                lineMapper,
                line.getName());
    }

    public boolean isExists(Line line){
        return this.jdbcTemplate.queryForObject(
                "select count(*) from line where name = ?", Integer.class, line.getName()) > 0;
    }

    public List<Line> findAll() {
        return this.jdbcTemplate.query(
                "select * from line",
                lineMapper
        );
    }

    public Line getById(Long id) {
        return this.jdbcTemplate.queryForObject(
                "select * from line where id = ?",
                lineMapper,
                id
        );
    }

    // TODO 변경할 이름이 이미 존재할 경우 - 테스트 하나 만들어서 해볼것.
    public void update(Long id, Line line) {
        this.jdbcTemplate.update("update line set name = ?, color = ? where id = ?",
                line.getName(), line.getColor(), id);
    }

    public boolean deleteById(Long id) {
        return this.jdbcTemplate.update("delete from line where id = ?", id) > 0;
    }
}
