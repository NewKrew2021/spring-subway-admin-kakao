package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.station.Station;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
public class SectionDao {
//    private Map<Long, List<Section>> sections = new HashMap<>();
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;

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

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

//    public void save(Long id, Section section) {
//        sections.put(id, new LinkedList<>(Arrays.asList(section)));
//    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public int update(Section section) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        return jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public Section insert(Section section) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(section);
        Number number = insertActor.executeAndReturnKey(sqlParameterSource);
        return new Section(number.longValue(), section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Section findByUpStationId(Long upStationId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where up_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, upStationId);
    }

    public Section findByDownStationId(Long downStationId) {
        String sql = "select line_id, up_station_id, down_station_id, distance from SECTION where down_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, downStationId);
    }

//    private void checkSameStations(Section newSection, Section oldSection) {
//        if (newSection.getUpStationId() == oldSection.getUpStationId() && newSection.getDownStationId() == oldSection.getDownStationId()) {
//            throw new IllegalArgumentException();
//        }
//    }
//
//    private void checkDistanceValidation(int newDistance, int oldDistance){
//        if(newDistance >= oldDistance){
//            throw new IllegalArgumentException();
//        }
//    }
//
//    private void updateSection(int index, Section newSection) {
//        List<Section> updateSections = this.sections.get(newSection.getLineId());
//        updateSections.remove(index);
//        updateSections.add(index, newSection);
//    }
//
//    private void insertSection(int index, Section section) {
//        List<Section> sections = this.sections.get(section.getLineId());
//        sections.add(index, section);
//    }

//    public void addSection(int index, Long id, Section section) {
//        List<Section> updateSections = sections.get(section.getLineId());
//        if(id == section.getUpStationId()){
//            if(index == updateSections.size()){
//                Section newSection = new Section(section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
//                insertSection(index, newSection);
//                return;
//            }
//            checkSameStations(updateSections.get(index), section);
//            int oldDistance = updateSections.get(index).getDistance();
//            int newDistance = oldDistance - section.getDistance();
//            checkDistanceValidation(newDistance, oldDistance);
//            insertSection(index, section);
//            Section newSection = new Section(section.getLineId(), section.getDownStationId(), updateSections.get(index).getDownStationId(), newDistance);
//            updateSection(index+1, newSection);
//        }
//        if (id == section.getDownStationId()) {
//            if (index == 0) {
//                Section newSection = new Section(section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
//                insertSection(index, newSection);
//                return;
//            }
//            checkSameStations(updateSections.get(index), section);
//            int oldDistance = updateSections.get(index).getDistance();
//            int newDistance = oldDistance - section.getDistance();
//            checkDistanceValidation(newDistance, oldDistance);
//            insertSection(index, section);
//            Section newSection = new Section(section.getLineId(), updateSections.get(index).getUpStationId(), section.getUpStationId(), newDistance);
//            updateSection(index-1, newSection);
//        }
//    }
    // bc
    // a c d ac cd -> ab bc cd
}
