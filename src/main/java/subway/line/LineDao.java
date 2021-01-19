package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        String SQL = "INSERT INTO line(name,color) VALUES (?,?)";
        String SELECT_SQL = "SELECT * FROM line where name = ?";
        jdbcTemplate.update(SQL, line.getName(), line.getColor());
        return jdbcTemplate.queryForObject(
                SELECT_SQL,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                ),
                line.getName()
        );
    }

    public boolean hasContains(Line line) {
        String SQL = "SELECT * FROM line";
        List<Line> lines = jdbcTemplate.query(
                SQL,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                )
        );
        return lines.contains(line);
    }


    public List<Line> getLines() {
        String SQL = "SELECT * FROM line";
        return jdbcTemplate.query(
                SQL,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                )
        );
    }

    public Line getLine(long id) {
        String SQL = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(
                SQL,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                ),
                id
        );
    }

    public void editLineById(long id, String name, String color) {
        String SQL = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(SQL, name, color, id);
    }


    public void deleteLineById(long id) {
        String SQL = "DELETE FROM line where id = ?";
        jdbcTemplate.update(SQL, id);
    }
}
