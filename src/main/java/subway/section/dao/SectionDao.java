package subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.section.domain.Section;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SectionDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    SectionMapper sectionMapper;

    public void save(Section section) {
        jdbcTemplate.update(SectionQuery.SAVE.getQuery(),
                section.getLineId(), section.getUpStationId(),
                section.getDownStationId(), section.getDistance());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(SectionQuery.DELETE_BY_ID.getQuery(), id);
    }

    public void deleteBySectionList(List<Section> sectionList) {
        for (Section section : sectionList) {
            jdbcTemplate.update(SectionQuery.DELETE_BY_ID.getQuery(), section.getId());
        }
    }

    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query(SectionQuery.FIND_BY_LINE_ID.getQuery(), sectionMapper, lineId);
    }

    public void update(Section updateSection) {
        jdbcTemplate.update(SectionQuery.UPDATE.getQuery(),
                updateSection.getUpStationId(), updateSection.getDownStationId(),
                updateSection.getDistance(), updateSection.getId());
    }

    public List<Section> findByStationIdAndLineId(Long stationId, Long lineId) {
        return jdbcTemplate.query(SectionQuery.FIND_BY_STATION_ID_AND_LINE_ID.getQuery(),
                sectionMapper, lineId, stationId, stationId);
    }
}
