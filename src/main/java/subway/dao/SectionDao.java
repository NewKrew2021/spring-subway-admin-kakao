package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.DeleteImpossibleException;

import java.util.*;

@Repository
public class SectionDao {
    public static final String SELECT_FROM_SECTION_WHERE_LINE_ID = "select * from SECTION where line_id = ?";
    public static final String SELECT_FROM_STATION_WHERE_ID = "select * from STATION where id = ?";
    public static final String DELETE_FROM_SECTION_WHERE_ID = "delete from SECTION where id = ?";
    public static final String DELETE_FROM_SECTION_WHERE_LINE_ID = "delete from SECTION where line_id = ?";
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("section")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getLineId());
    }

    public void saveSections(Sections sections) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("section")
                .usingGeneratedKeyColumns("id");
        for (Section section : sections.getSections()) {
            simpleJdbcInsert.execute(new BeanPropertySqlParameterSource(section));
        }
    }

    public Sections getSectionsByLineId(Long lineId) {
        String sql = SELECT_FROM_SECTION_WHERE_LINE_ID;
        Sections sections = new Sections(jdbcTemplate.query(sql, (rs, rowNum) -> new Section(rs.getLong("id"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                rs.getInt("distance"),
                rs.getLong("line_id")), lineId));
        return new Sections(sections.getSections(), getStations(sections));
    }

    private List<Station> getStations(Sections sections) {
        String sql = SELECT_FROM_STATION_WHERE_ID;
        List<Station> stations = new ArrayList<>();
        Set<Long> stationIds = getStationIds(sections);
        for (Long stationId : stationIds) {
            stations.add(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")), stationId));
        }
        return stations;
    }

    private Set<Long> getStationIds(Sections sections) {
        Set<Long> stationIds = new LinkedHashSet<>();
        for (Section section : sections.getSections()) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        return stationIds;
    }

    public void deleteSectionById(Long sectionId) {
        String sql = DELETE_FROM_SECTION_WHERE_ID;
        if (jdbcTemplate.update(sql, sectionId) == 0) {
            throw new DeleteImpossibleException();
        }
    }

    public void deleteSectionByLineId(Long lineId) {
        String sql = DELETE_FROM_SECTION_WHERE_LINE_ID;
        jdbcTemplate.update(sql, lineId);
    }
}
