package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Line save(Line line) {
        String sql = "insert into line (name, color) values (?,?)";

        KeyHolder keyHoler = new GeneratedKeyHolder();

        jdbcTemplate.update(e -> {
            PreparedStatement preparedStatement = e.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());

            return preparedStatement;
        }, keyHoler);

        Long id = (long) keyHoler.getKey();
        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) ->
                        new Line(
                                resultSet.getLong("id"),
                                resultSet.getString("name"),
                                resultSet.getString("color")
                        )
        );
    }


    public Line findById(Long id) {
        String sql = "select * from line where id = ?";
        return jdbcTemplate.queryForObject(
                sql,
                (resultSet, rowNum) ->
                        new Line(
                                resultSet.getLong("id"),
                                resultSet.getString("name"),
                                resultSet.getString("color")
                        )
                , id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from line where id = ?", id);
    }


    public void modify(Long id, LineRequest lineRequest) {
        jdbcTemplate.update("update line set name = ?, color = ? where id = ?",
                lineRequest.getName(), lineRequest.getColor(), id);
    }

    public int findByName(String name) {
        String sql = "select count(*) from line where name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }
}
