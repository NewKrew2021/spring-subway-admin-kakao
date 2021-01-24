package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Sections;
import subway.exception.AlreadyExistDataException;
import subway.exception.DeleteImpossibleException;
import subway.exception.UpdateImpossibleException;

import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SectionDao sectionDao;

    public LineDao(JdbcTemplate jdbcTemplate, SectionDao sectionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionDao = sectionDao;
    }

    public Line save(Line line) {
        Long lineId;
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("line")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        try {
            lineId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        } catch (RuntimeException e) {
            throw new AlreadyExistDataException();
        }
        return new Line(lineId, line.getName(), line.getColor());

    }

    public void deleteById(Long lineId) {
        String sql = "delete from LINE where id = ?";
        if (jdbcTemplate.update(sql, lineId) == 0) {
            throw new DeleteImpossibleException();
        }
    }

    public List<Line> findAll() {
        String sql = "select id from LINE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> findOne(rs.getLong("id")));
    }

    public Line findOne(Long lineId) {
        String getLineSql = "select * from LINE where id = ?";
        Line line = jdbcTemplate.queryForObject(getLineSql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")), lineId);
        Sections sections = sectionDao.getSectionsByLineId(lineId);
        return new Line(line.getId(), line.getName(), line.getColor(), sections);
    }

    public void update(Line line) {
        String sql = "update LINE set color = ?, name = ? where id = ?";
        if (jdbcTemplate.update(sql, line.getColor(), line.getName(), line.getId()) == 0) {
            throw new UpdateImpossibleException();
        }
    }
}
