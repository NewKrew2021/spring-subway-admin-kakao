package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.line.Line;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;

    private final int MIDDLE_STATION_DELETE_SIZE = 2;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        deleteExistSection(section);
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
                rs.getLong("line_id")), lineId), lineId);
    }

    private int deleteExistSection(Section section){
        String sql = "delete from SECTION where up_station_id = ? or down_station_id = ?";
        return jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId());
    }

    public int deleteSectionById(Long sectionId) {
        String sql = "delete from SECTION where id = ?";
        return jdbcTemplate.update(sql, sectionId);
    }
    
    public Sections saveSections(Sections sections){
        return new Sections(sections.getSections()
                .stream()
                .map(section -> save(section))
                .collect(Collectors.toList()), sections.getLineId()
        );
    }

    public List<Integer> deleteSections(Sections sections){
        saveWhenStationIsMiddle(sections);
        return sections.getSections()
                .stream()
                .map(section -> deleteSectionById(section.getSectionId()))
                .collect(Collectors.toList());
    }

    private void saveWhenStationIsMiddle(Sections sections){
        if(hasMiddleStationToDelete(sections)){
            Long s1UpStationId = sections.getSections().get(0).getUpStationId();
            Long s1DownStationId = sections.getSections().get(0).getDownStationId();
            Long s2UpStationId = sections.getSections().get(1).getUpStationId();
            Long s2DownStationId = sections.getSections().get(1).getDownStationId();

            Long midStationId = (s1DownStationId == s2UpStationId) ? s1DownStationId : s1UpStationId;
            Long fromStationId = (midStationId == s1UpStationId) ? s2UpStationId : s2DownStationId;
            Long toStationId = (midStationId == s1UpStationId) ? s1DownStationId : s1UpStationId;

            Section middleSection = new Section(
                    fromStationId,
                    toStationId,
                    sections.getSections().get(0).getDistance() + sections.getSections().get(1).getDistance(),
                    sections.getLineId());

            save(middleSection);
        }
    }

    private boolean hasMiddleStationToDelete(Sections delSections){
        return delSections.getSections().size() == MIDDLE_STATION_DELETE_SIZE;
    }
}
