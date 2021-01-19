package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.line.Line;

import java.util.List;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existSection(Section section) {
        String sql = "select count(*) from section where up_station_id = ? and down_station_id = ? and line_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class,
                section.getUpStationId(),
                section.getDownStationId(),
                section.getLineId()) > 0;
    }

    public Section save(Section section) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id,
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                section.getLineId());
    }

    public Sections getSectionsByLineId(Long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, (rs, rowNum) -> new Section(rs.getLong("id"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                rs.getInt("distance"),
                rs.getLong("line_id")), lineId));
    }

    public int deleteSectionById(Long sectionId) {
        String sql = "delete from SECTION where id = ?";
        return jdbcTemplate.update(sql, sectionId);
    }
}
