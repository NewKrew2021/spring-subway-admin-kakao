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
    private final SectionDao sectionDao;

    private final JdbcTemplate jdbcTemplate;

    public LineDao(SectionDao sectionDao, JdbcTemplate jdbcTemplate) {
        this.sectionDao = sectionDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        String sql = "insert into line (name, color) values (?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, line.getName());
            pstmt.setString(2, line.getColor());
            return pstmt;
        }, keyHolder);
        return new Line(
                keyHolder.getKey().longValue(),
                line.getName(),
                line.getColor());
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
                        resultSet.getString("color"),
                        sectionDao.findAll(id)
                ), id);
    }

    public Line update(Long id, Line line) {
        String sql = "update line set name = ?, color = ? where id = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, line.getName());
            pstmt.setString(2, line.getColor());
            pstmt.setLong(3, id);
            return pstmt;
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
