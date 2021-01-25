package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.dao.queries.SectionQuery;
import subway.domain.section.Section;

import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
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

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Section section) {
        jdbcTemplate.update(
                SectionQuery.insert,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        );
    }

    public void update(Section section){
        jdbcTemplate.update(SectionQuery.update, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(SectionQuery.deleteById, id);
    }

    public List<Section> findAllByLineId(Long id){
        return jdbcTemplate.query(SectionQuery.selectByLineId, sectionRowMapper, id);
    }
}