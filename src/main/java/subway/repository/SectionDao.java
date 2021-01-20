package subway.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

import java.util.List;

@Repository
public class SectionDao {
    private static final String SAVE_SQL = "insert into SECTION (line_id, up_station_id, down_station_id, distance, first_section, last_section) values (?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_LINE_ID_SQL = "select id, line_id, up_station_id, down_station_id, distance, first_section, last_section from SECTION where line_id = ?";
    private static final String FIND_BY_STATION_ID_AND_LINE_ID_SQL = "select id, line_id, up_station_id, down_station_id, distance, first_section, last_section from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)";
    private static final String FIND_FIRST_BY_LINE_ID_SQL = "select id, line_id, up_station_id, down_station_id, distance, first_section, last_section from SECTION where line_id = ? and first_section";
    private static final String FIND_LAST_BY_LINE_ID_SQL = "select id, line_id, up_station_id, down_station_id, distance, first_section, last_section from SECTION where line_id = ? and last_section";
    private static final String UPDATE_SQL = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ?, first_section = ?, last_section = ? where id = ?";
    private static final String DELETE_BY_ID_SQL = "delete from SECTION where id = ?";
    private static final String DELETE_BY_LINE_ID_SQL = "delete from SECTION where line_id = ?";
    private static final String COUNT_BY_LINE_ID_SQL = "select count(*) from SECTION where line_id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final SectionMapper sectionMapper;

    public SectionDao(JdbcTemplate jdbcTemplate, SectionMapper sectionMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionMapper = sectionMapper;
    }

    public void save(Section section) {
        jdbcTemplate.update(SAVE_SQL,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                section.isFirstSection(),
                section.isLastSection());
    }

    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query(FIND_BY_LINE_ID_SQL, sectionMapper, lineId);
    }

    public List<Section> findByStationIdAndLineId(Long stationId, Long lineId) {
        return jdbcTemplate.query(FIND_BY_STATION_ID_AND_LINE_ID_SQL, sectionMapper, lineId, stationId, stationId);
    }

    public Section findFirstByLineId(Long lineId) {
        return jdbcTemplate.queryForObject(FIND_FIRST_BY_LINE_ID_SQL, sectionMapper, lineId);
    }

    public Section findLastByLineId(Long lineId) {
        return jdbcTemplate.queryForObject(FIND_LAST_BY_LINE_ID_SQL, sectionMapper, lineId);
    }

    public void update(Section updateSection) {
        jdbcTemplate.update(UPDATE_SQL,
                updateSection.getUpStationId(),
                updateSection.getDownStationId(),
                updateSection.getDistance(),
                updateSection.isFirstSection(),
                updateSection.isLastSection(),
                updateSection.getId());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_SQL, id);
    }

    public void deleteByLineId(Long lineId) {
        jdbcTemplate.update(DELETE_BY_LINE_ID_SQL, lineId);
    }

    public int countByLineId(Long lineId) {
        return jdbcTemplate.queryForObject(COUNT_BY_LINE_ID_SQL, Integer.class, lineId);
    }
}
