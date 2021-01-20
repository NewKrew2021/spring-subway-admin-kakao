package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.exception.AlreadyExistDataException;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LineDao {
    private List<Line> lines = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    public LineDao(SectionDao sectionDao, JdbcTemplate jdbcTemplate) {
        this.sectionDao = sectionDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("line")
                    .usingGeneratedKeyColumns("id");
            SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
            Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            return new Line(id, line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId());
        } catch (Exception e) {
            throw new AlreadyExistDataException();
        }
    }

    public int deleteById(Long lineId) {
        String sql = "delete from LINE where id = ?";
        return jdbcTemplate.update(sql, lineId);
    }

    public List<Line> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color"), rs.getLong("up_station_id"), rs.getLong("down_station_id")));
    }

    public Line findOne(Long lineId) {
        String sql = "select * from LINE where id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color"), rs.getLong("up_station_id"), rs.getLong("down_station_id")), lineId);
    }

    public int update(Line line) {
        String sql = "update LINE set color = ?, name = ? where id = ?";
        return jdbcTemplate.update(sql, line.getColor(), line.getName(), line.getId());
    }

    public int updateAll(Line line) {
        String sql = "update LINE set color = ?, name = ?, up_station_id = ?, down_station_id = ? where id = ?";
        return jdbcTemplate.update(sql, line.getColor(), line.getName(), line.getUpStationId(), line.getDownStationId(), line.getId());
    }
}
