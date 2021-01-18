package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import subway.exceptions.*;
import java.util.stream.Collectors;import org.springframework.util.ReflectionUtils;
import subway.exceptions.InvalidValueException;
import subway.station.*;

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
        List<Section> sections = findByLineId(newSection.getLineId());

        // 중복체크
        if(sections.stream().anyMatch(section ->
                section.getUpStationId() == newSection.getUpStationId() &&
                        section.getDownStationId() == newSection.getDownStationId()
        )){ throw new InvalidValueException(); }

        // 추가하려고 하는 역들이 구간에 있는 역이 아닐 경우
        if(sections.size() > 0 && sections.stream().allMatch(section ->
                section.getUpStationId() != newSection.getUpStationId() &&
                section.getUpStationId() != newSection.getDownStationId() &&
                section.getDownStationId() != newSection.getUpStationId() &&
                section.getDownStationId() != newSection.getDownStationId()
                )){
            throw new InvalidValueException();
        }


        jdbcTemplate.update("delete FROM SECTION WHERE LINE_ID = ?", newSection.getLineId());

        // 추가될때 상행하행 이어붙이기
        int sectionLength = sections.size();
        for(int i=0; i < sectionLength ; i++){
            Section savedSection = sections.get(i);
            if(savedSection.getUpStationId() == newSection.getUpStationId()) {
                if(savedSection.getDistance() <= newSection.getDistance()){
                    throw new InvalidValueException();
                }
                savedSection.setUpStationId(newSection.getDownStationId());
                savedSection.setDistance(savedSection.getDistance() - newSection.getDistance());
            }
            if(savedSection.getDownStationId() == newSection.getUpStationId()){
                if(sections.get(i+1).getDistance() <= newSection.getDistance()){
                    throw new InvalidValueException();
                }
                savedSection.setDownStationId(newSection.getUpStationId());
                savedSection.setDistance(savedSection.getDistance() - newSection.getDistance());
            }
        }

        // 새로운 종점이 생기는 경우 이어붙이기
        for(int i = 0; i < sectionLength; i++) {
            if(sections.get(i).getUpStationId() == newSection.getDownStationId()){
                    sections.add(i, newSection);
                    break;
            }
        }


        // 처음인 녀석
        if (sections.size() == 0){
            sections.add(newSection);
        }

        Section returnSection = new Section();
        for (Section section : sections) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("LINE_ID", section.getLineId())
                    .addValue("UP_STATION_ID", section.getUpStationId())
                    .addValue("DOWN_STATION_ID", section.getDownStationId())
                    .addValue("DISTANCE", section.getDistance());
            Number id = simpleJdbcInsert.executeAndReturnKey(params);
            if(section == newSection)
                returnSection = findById(id.longValue());
        }
        return returnSection;
    }

    public List<Section> findAll() {
        return jdbcTemplate.query("select * from SECTION", (rs, rowNum) ->
                new Section(rs.getLong("line_id"),
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"),
                        rs.getInt("distance")));
    }

    public Section findById(Long id) {
        return jdbcTemplate.queryForObject("select * from SECTION where id = ?",
                (rs, rowNum) -> new Section(
                        rs.getLong("id"),
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"),
                        rs.getInt("distance")
                ),
                id);
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

    public void update(Long sectionId, Section section){
        jdbcTemplate.update("update section set up_station_id = ?, down_station_id = ?, distance = ?, line_id = ? where id = ?",
                new Object[]{section.getUpStationId(),
                        section.getDownStationId(),
                        section.getDistance(),
                        section.getLineId(),
                        sectionId});
    }

    public void deleteById(Long sectionId){
        jdbcTemplate.update("delete from section where id = ?", sectionId);
    }

    public boolean contain(Long stationId){
        return jdbcTemplate.query("select * from section where up_station_id = ? or down_station_id = ?",
                (rs, rowNum) -> new Section(
                        rs.getLong("id"),
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"),
                        rs.getInt("distance")
                ), new Object[]{stationId, stationId}).size() != 0;
    }

//    public Long ifDownIdExist(Long downId) {
//        if (sections.stream().filter(section -> section.getDownStationId() == downId).collect(Collectors.toList()).size() == 0)
//            return -1L;
//        return sections.stream().filter(section -> section.getDownStationId() == downId).collect(Collectors.toList()).get(0).getId();
//    }
//
//    public Long ifUpIdExist(Long upId) {
//        if (sections.stream().filter(section -> section.getUpStationId() == upId).collect(Collectors.toList()).size() == 0)
//            return -1L;
//        return sections.stream().filter(section -> section.getUpStationId() == upId).collect(Collectors.toList()).get(0).getId();
//    }

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

    public int getDistance(Long lineId) {
        List<Section> sections = findByLineId(lineId);
        int distance = 0;
        for (Section section : sections){
            distance += section.getDistance();
        }
        return distance;
    }
}
