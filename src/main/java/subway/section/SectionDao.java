package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@Repository
public class SectionDao {
    private static final String FIND_BY_STATION_ID_SQL = "SELECT * FROM section WHERE station_id = ?";
    private static final String FIND_BY_LINE_ID_SQL = "SELECT * FROM section WHERE line_id = ? ORDER BY distance";
    private static final String DELETE_BY_STATION_ID_SQL = "DELETE FROM section WHERE station_id = ?";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public void save(Section newSection) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("LINE_ID", newSection.getLineId())
                .addValue("STATION_ID", newSection.getStationId())
                .addValue("DISTANCE", newSection.getDistance());
        simpleJdbcInsert.executeAndReturnKey(params);
    }

    public List<Section> findByStationId(Long stationId) {
        return jdbcTemplate.query(FIND_BY_STATION_ID_SQL, sectionRowMapper, stationId);
    }

    public void deleteById(Long stationId){
        jdbcTemplate.update(DELETE_BY_STATION_ID_SQL, stationId);
    }

    public List<Section> findByLineId(Long lineId){
        return jdbcTemplate.query(FIND_BY_LINE_ID_SQL, sectionRowMapper, lineId);
    }

    private final RowMapper<Section> sectionRowMapper = (rs, rowNum) -> {
        Section section = new Section(rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("station_id"),
            rs.getInt("distance"));
        return section;
    };

}
