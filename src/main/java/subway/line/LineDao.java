package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;
import subway.station.Station;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        String sql = "insert into line (name, color) values (?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        return new Line(
                keyHolder.getKey().longValue(),
                line.getName(),
                line.getColor());
    }

    private Line updateOldObject(Line sourceLine, Line destLine) {
        Arrays.stream(Line.class.getDeclaredFields())
                .filter(field -> !field.getName().equals("id"))
                .forEach(field -> {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field,
                            destLine,
                            ReflectionUtils.getField(field, sourceLine));
                });
        return destLine;
    }


    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                ));
    }

    public Line findOne(Long id) {
        String sql = "select id, name, color from line where id = ?";
        return jdbcTemplate.queryForObject(
                sql,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                ), id);
    }

    public Line update(Long id, Line line) {
        String sql = "update line set name = ?, color = ? where id = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            ps.setLong(3, id);
            return ps;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(),
                line.getName(),
                line.getColor());
    }

    public void deleteById(Long id) {
        String sql = "delete from line where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        String sql = "delete from line";
        jdbcTemplate.update(sql);
    }
}
