package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;
import subway.exceptions.InvalidValueException;

@Repository
public class SectionDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Section newSection) {
        AlignSections sections = new AlignSections(findByLineId(newSection.getLineId()));
        sections.addSection(newSection);

        jdbcTemplate.update("delete FROM SECTION WHERE LINE_ID = ?", newSection.getLineId());
        sections.applyToAllSection((Section section) -> {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("LINE_ID", section.getLineId())
                    .addValue("UP_STATION_ID", section.getUpStationId())
                    .addValue("DOWN_STATION_ID", section.getDownStationId())
                    .addValue("DISTANCE", section.getDistance());
            simpleJdbcInsert.executeAndReturnKey(params);
        });

        return sections.findByStationId(newSection.getUpStationId(), newSection.getDownStationId());
    }

    public List<Section> findByStationId(Long stationId) {
        return jdbcTemplate.query("select * from SECTION where up_station_id = ? or down_station_id = ?",
                (rs, rowNum) -> new Section(
                        rs.getLong("id"),
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"),
                        rs.getInt("distance")
                ),
                stationId, stationId);
    }

    public void deleteById(Long lineId, Long stationId) {
        List<Section> sections = findByLineId(lineId);

        int sectionsLength = sections.size();
        // 구간이 1개인 노선인 경우
        if (sections.size() == 1){
            throw new InvalidValueException();
        }

        // 가장 왼쪽인 경우
        if (sections.get(0).getUpStationId() == stationId){
            deleteById(sections.get(0).getId());
            return;
        }

        // 가장 오른쪽인 경우
        if (sections.get(sections.size()-1).getDownStationId() == stationId){
            deleteById(sections.get(sections.size()-1).getId());
            return;
        }

        // 중간에 있는 경우
        for (int i = 0; i < sectionsLength; i++) {
            if (sections.get(i).getDownStationId() == stationId) {
                Section leftSection = sections.get(i);
                Section rightSection = sections.get(i+1);

                update(sections.get(i).getId(), new Section(
                        sections.get(i).getLineId(), // line
                        sections.get(i).getUpStationId(), // up
                        sections.get(i+1).getDownStationId(), // down
                        sections.get(i).getDistance() + sections.get(i+1).getDistance() // dis
                ));

                deleteById(sections.get(i+1).getId());
                break;
            }
        }

    }

    public void deleteById(Long sectionId){
        jdbcTemplate.update("delete from section where id = ?", sectionId);
    }

    public void deleteByLineId(Long lineId){
        jdbcTemplate.update("delete from section where line_id = ?", lineId);
    }

    public void update(Long sectionId, Section section){
        jdbcTemplate.update("update section set up_station_id = ?, down_station_id = ?, distance = ?, line_id = ? where id = ?",
                        section.getUpStationId(),
                        section.getDownStationId(),
                        section.getDistance(),
                        section.getLineId(),
                        sectionId);
    }

    public List<Section> findByLineId(Long lineId){
        return jdbcTemplate.query("SELECT * FROM SECTION WHERE line_id = ?",
                (rs, rowNum) -> new Section(
                        rs.getLong("id"),
                        rs.getLong("line_id"),
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"),
                        rs.getInt("distance")),
                lineId);
    }

    public Long getDownStationId(Long lineId){
        List<Section> sections = findByLineId(lineId);
        return sections.get(sections.size()-1).getDownStationId();
    }

    public Long getUpStationId(Long lineId){
        List<Section> sections = findByLineId(lineId);
        return sections.get(0).getUpStationId();
    }
    }
