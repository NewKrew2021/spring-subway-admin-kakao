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
import java.util.stream.Collectors;

@Repository
public class SectionDao {
    public static final String SELECT_FROM_SECTION_WHERE_LINE_ID = "select SE.id as id, SE.distance as distance, SE.line_id as line_id, " +
            "UST.id as up_station_id, UST.name as uname, " +
            "DST.id as down_station_id, DST.name as dname " +
            "from SECTION SE left outer join STATION UST on SE.up_station_id = UST.id " +
            "left outer join STATION DST on SE.down_station_id = DST.id " +
            "where line_id = ?";
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
        Long id = simpleJdbcInsert.executeAndReturnKey(getSectionParameter(section)).longValue();
        return new Section(id, section.getUpStation(), section.getDownStation(), section.getDistance(), section.getLineId());
    }

    public void saveSections(Sections sections) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("section")
                .usingGeneratedKeyColumns("id");
        for (Section section : sections.getSections()) {
            simpleJdbcInsert.execute(getSectionParameter(section));
        }
    }

    private Map<String, Object> getSectionParameter(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("line_id", section.getLineId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());
        return params;
    }

    public Sections getSectionsByLineId(Long lineId) {
        String sql = SELECT_FROM_SECTION_WHERE_LINE_ID;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, lineId);
        Sections sections = new Sections(list.stream()
                .collect(Collectors.groupingBy(it -> it.get("id"))).values().stream()
                .map(maps -> new Section((Long) maps.get(0).get("id"),
                        new Station((Long) maps.get(0).get("up_station_id"), maps.get(0).get("uname").toString()),
                        new Station((Long) maps.get(0).get("down_station_id"), maps.get(0).get("dname").toString()),
                        (int) maps.get(0).get("distance"), (Long) maps.get(0).get("line_id")))
                .collect(Collectors.toList()));
        return sections;
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
