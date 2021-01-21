package subway.section.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.section.domain.Section;
import subway.section.domain.Sections;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SectionMapper sectionMapper;

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate, SectionMapper sectionMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionMapper = sectionMapper;
    }

    public Long save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(SectionQuery.SAVE.getQuery(), new String[]{"id"});

            preparedStatement.setLong(1, section.getLineId());
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setInt(4, section.getDistance());

            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(SectionQuery.DELETE_BY_ID.getQuery(), id);
    }

    public void deleteBySections(Sections sections) {
        for (Section section : sections.getSections()) {
            jdbcTemplate.update(SectionQuery.DELETE_BY_ID.getQuery(), section.getId());
        }
    }

    public List<Section> findByStationId(Long stationId) {
        return jdbcTemplate.query(SectionQuery.FIND_BY_STATION_ID.getQuery(), sectionMapper, stationId, stationId);
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
