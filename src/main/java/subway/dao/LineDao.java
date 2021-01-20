package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return line;
    };

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "insert into line (name, color) values (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, line.getName());
                ps.setString(2, line.getColor());
                return ps;
            }, keyHolder);

            Long id = keyHolder.getKey().longValue();
            return new Line(id, line.getName(), line.getColor());
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException("동일한 이름을 가진 노선이 이미 존재합니다.");
        }
    }

    public Line findOne(Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from line where id = ?", lineRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoContentException("해당 id를 갖는 노선이 존재하지 않습니다.");
        }
    }

    public List<Line> findAll() {
        return jdbcTemplate.query("select * from line", lineRowMapper);
    }

    public void update(Line line) {
        int updateResult = jdbcTemplate.update("update line set name = ?, color = ? where id = ?", line.getName(), line.getColor(), line.getId());
        if (updateResult == 0) {
            throw new NoContentException("해당 id를 갖는 노선이 존재하지 않습니다.");
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from line where id = ?", id);
    }
}
