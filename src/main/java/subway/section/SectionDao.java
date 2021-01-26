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

    public int deleteSectionByUpStationId(Long upStationId, Long lineId) {
        String sql = "delete from SECTION where up_station_id = ? and line_id = ?";
        return jdbcTemplate.update(sql, upStationId, lineId);
    }

    public int deleteSectionByDownStationId(Long downStationId, Long lineId) {
        String sql = "delete from SECTION where down_station_id = ? and line_id = ?";
        return jdbcTemplate.update(sql, downStationId, lineId);
    }

    public Sections saveSections(Sections sections){
        return new Sections(sections.getSections()
                .stream()
                .map(section -> save(section))
                .collect(Collectors.toList()), sections.getLineId()
        );
    }

    public List<Integer> deleteSections(Sections sections){
        if(sections.getSections().size() == 2){
            Long midStationId =
                    sections.getSections().get(0).getDownStationId() == sections.getSections().get(1).getUpStationId()
                    ? sections.getSections().get(0).getDownStationId()
                    : sections.getSections().get(0).getUpStationId();

            Long fromStationId =
                    midStationId == sections.getSections().get(0).getUpStationId()
                            ? sections.getSections().get(1).getUpStationId()
                            : sections.getSections().get(1).getDownStationId();

            Long toStationId =
                    midStationId == sections.getSections().get(0).getUpStationId()
                            ? sections.getSections().get(0).getDownStationId()
                            : sections.getSections().get(0).getUpStationId();

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("section")
                    .usingGeneratedKeyColumns("id");

            SqlParameterSource parameters = new BeanPropertySqlParameterSource(new Section(
                    fromStationId,
                    toStationId,
                    sections.getSections().get(0).getDistance() + sections.getSections().get(1).getDistance(),
                    sections.getLineId()));
            simpleJdbcInsert.execute(parameters);
        }
        return sections.getSections()
                .stream()
                .map(section -> deleteSectionById(section.getSectionId()))
                .collect(Collectors.toList());
    }
}
