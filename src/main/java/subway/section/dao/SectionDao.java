package subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.section.domain.Section;
import subway.section.query.SectionQuery;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public Section save(Section section){

        KeyHolder keyHoler = new GeneratedKeyHolder();

        jdbcTemplate.update(e -> {
            PreparedStatement preparedStatement = e.prepareStatement(
                    SectionQuery.INSERT, java.sql.Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, section.getLineId());
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setLong(4, section.getDistance());
            return preparedStatement;
        }, keyHoler);

        Long id = (long) keyHoler.getKey();
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(SectionQuery.DELETE_BY_ID, id);
    }

    public List<Section> findSectionsByLineId(Long lineId){
        return jdbcTemplate.query(SectionQuery.SELECT_BY_LINE, sectionRowMapper, lineId);
    }


    public Section findSectionByLineIdAndDownStationId(Long lineId, Long stationId) {
        return jdbcTemplate.queryForObject(SectionQuery.SELECT_BY_LINE_AND_DOWNSTATION, sectionRowMapper, lineId, stationId);
    }

    public Section findSectionByLineIdAndUpStationId(Long lineId, Long stationId) {
        return jdbcTemplate.queryForObject(SectionQuery.SELECT_BY_LINE_AND_UPSTATION, sectionRowMapper, lineId, stationId);
    }

    public int countByLineId(Long id) {
        return jdbcTemplate.queryForObject(SectionQuery.COUNT_BY_LINE, Integer.class, id);
    }
}
