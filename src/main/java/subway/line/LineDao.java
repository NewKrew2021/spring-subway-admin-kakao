package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.section.Section;
import subway.section.SectionDao;

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
            return new Line(id, line.getColor(), line.getName());

        }
        catch(Exception e){
            return null;
        }
    }

    public int deleteById(Long lineId) {
        String sql = "delete from LINE where id = ?";
        return jdbcTemplate.update(sql, lineId);
    }

    public List<Line> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("color"), rs.getString("name"), rs.getLong("up_station_id"), rs.getLong("down_station_id")));
    }

    public Line findOne(Long lineId) {
        String sql = "select * from LINE where id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("color"), rs.getString("name"), rs.getLong("up_station_id"), rs.getLong("down_station_id")), lineId);
    }

    public int update(Line line) {
        String sql = "update LINE set color = ?, name = ?, up_station_id = ?, down_station_id = ? where id = ?";
        return jdbcTemplate.update(sql, line.getColor(), line.getName(), line.getUpStationId(), line.getDownStationId(), line.getId());
    }
}
