package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SectionDao {

    public static final String INSERT_SECTION = "insert into SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?,?,?,?)";
    public static final String UPDATE_SECTION_BY_ID = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
    public static final String DELETE_SECTION_BY_LINE_ID = "delete from section where line_id = ?";
    public static final String DELETE_SECTION_BY_ID = "delete from SECTION where id = ?";
    public static final String SELECT_SECTIONS_BY_LINE_ID = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ?";
    public static final String SELECT_SECTION_BY_LINE_ID_UP_DOWN_STATION_ID = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ? and up_station_id = ? and down_station_id = ?";

    private JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
        return section;
    };

    public Optional<Section> save(Section section) {
        jdbcTemplate.update(INSERT_SECTION, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
        return findById(section);
    }

    public Optional<Section> findById(Section section) {
        List<Section> result = jdbcTemplate.query(SELECT_SECTION_BY_LINE_ID_UP_DOWN_STATION_ID, sectionRowMapper,
                section.getLineId(), section.getUpStationId(), section.getDownStationId());
        return result.stream().findAny();
    }

    public int updateById(Section section) {
        return jdbcTemplate.update(UPDATE_SECTION_BY_ID, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public int deleteAllByLineId(Long lineId) {
        return jdbcTemplate.update(DELETE_SECTION_BY_LINE_ID, lineId);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update(DELETE_SECTION_BY_ID, id);
    }

    public List<Section> findAllSectionsByLineId(Long lineId) {
        return jdbcTemplate.query(SELECT_SECTIONS_BY_LINE_ID, sectionRowMapper, lineId);
    }

}
