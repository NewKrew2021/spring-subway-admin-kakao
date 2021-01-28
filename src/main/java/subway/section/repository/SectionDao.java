package subway.section.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;

import subway.exceptions.InvalidValueException;
import subway.section.domain.Section;

@Repository
public class SectionDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;
    private RowMapper<Section> rowMapper = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance"));

    private final String DELETE_BY_ID_QUERY = "delete from section where id = ?";
    private final String DELETE_BY_LINE_ID_QUERY = "delete from section where line_id = ?";
    private final String FIND_BY_SECTION_ID_QUERY = "select * from SECTION where id = ?";
    private final String FIND_BY_STATION_ID_QUERY = "select * from SECTION where up_station_id = ? or down_station_id = ?";
    private final String FIND_BY_LINE_ID_QUERY = "SELECT * FROM SECTION WHERE line_id = ?";

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Section newSection) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("LINE_ID", newSection.getLineId())
                .addValue("UP_STATION_ID", newSection.getUpStationId())
                .addValue("DOWN_STATION_ID", newSection.getDownStationId())
                .addValue("DISTANCE", newSection.getDistance());
        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
                return findById(id);
    }

    public Section findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_SECTION_ID_QUERY, rowMapper, id);
    }

    public List<Section> findByStationId(Long stationId) {
        return jdbcTemplate.query(FIND_BY_STATION_ID_QUERY,
                rowMapper,
                stationId, stationId);
    }

    public void deleteById(Long lineId, Long stationId) {
        List<Section> sections = findByLineId(lineId);

        int sectionsLength = sections.size();
        if (sections.size() == 1) {
            throw new InvalidValueException();
        }

        if (sections.get(0).getUpStationId() == stationId) {
            deleteById(sections.get(0).getId());
            return;
        }

        if (sections.get(sections.size() - 1).getDownStationId() == stationId) {
            deleteById(sections.get(sections.size() - 1).getId());
            return;
        }

        for (int i = 0; i < sectionsLength; i++) {
            if (sections.get(i).getDownStationId() == stationId) {
                Section leftSection = sections.get(i);
                Section rightSection = sections.get(i + 1);

                update(sections.get(i).getId(), new Section(
                        sections.get(i).getLineId(), // line
                        sections.get(i).getUpStationId(), // up
                        sections.get(i + 1).getDownStationId(), // down
                        sections.get(i).getDistance() + sections.get(i + 1).getDistance() // dis
                ));

                deleteById(sections.get(i + 1).getId());
                break;
            }
        }

    }

    public void deleteById(Long sectionId) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, sectionId);
    }

    public void deleteByLineId(Long lineId) {
        jdbcTemplate.update(DELETE_BY_LINE_ID_QUERY, lineId);
    }

    public void update(Long sectionId, Section section) {
        jdbcTemplate.update("update section set up_station_id = ?, down_station_id = ?, distance = ?, line_id = ? where id = ?",
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                section.getLineId(),
                sectionId);
    }

    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query(FIND_BY_LINE_ID_QUERY,
                rowMapper,
                lineId);
    }

    public Long getDownStationId(Long lineId) {
        List<Section> sections = findByLineId(lineId);
        return sections.get(sections.size() - 1).getDownStationId();
    }

    public Long getUpStationId(Long lineId) {
        List<Section> sections = findByLineId(lineId);
        return sections.get(0).getUpStationId();
    }
}
