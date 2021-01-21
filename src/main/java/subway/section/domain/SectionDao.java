package subway.section.domain;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.section.domain.Section;

import java.util.List;

@Repository
public class SectionDao {

    private static final String SELECT_FROM_SECTION_WHERE_LINE_ID = "select * from section where line_id = ? order by relative_position";
    private static final String DELETE_FROM_SECTION_WHERE_LINE_ID_AND_STATION_ID = "delete from section where line_id = ? and station_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final static RowMapper<Section> sectionMapper = ((rs, rowNum) ->
            new Section(rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("station_id"),
                    rs.getInt("relative_position")));

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public void save(Section section) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("LINE_ID", section.getLineId())
                .addValue("STATION_ID", section.getStationId())
                .addValue("relative_position", section.getRelativePosition());
        simpleJdbcInsert.executeAndReturnKey(params);
    }

    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query(SELECT_FROM_SECTION_WHERE_LINE_ID, sectionMapper, lineId);
    }

    public void deleteByLineIdAndStationId(Section section) {
        jdbcTemplate.update(DELETE_FROM_SECTION_WHERE_LINE_ID_AND_STATION_ID
                , section.getLineId()
                , section.getStationId());
    }

}
