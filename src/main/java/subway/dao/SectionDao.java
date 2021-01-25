package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.query.SectionQuery;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;
    private StationDao stationDao;

    public SectionDao(JdbcTemplate jdbcTemplate, LineDao lineDao, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                lineDao.findById(resultSet.getLong("line_id")),
                stationDao.findById(resultSet.getLong("up_station_id")),
                stationDao.findById(resultSet.getLong("down_station_id")),
                resultSet.getInt("distance")
        );
        return section;
    };

    public Section save(Section section){

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(e -> {
            PreparedStatement preparedStatement = e.prepareStatement(
                    SectionQuery.INSERT, java.sql.Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, section.getLine().getId());
            preparedStatement.setLong(2, section.getUpStation().getId());
            preparedStatement.setLong(3, section.getDownStation().getId());
            preparedStatement.setLong(4, section.getDistance());
            return preparedStatement;
        }, keyHolder);

        Long id = (long) keyHolder.getKey();
        return new Section(id, section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(SectionQuery.DELETE_BY_ID, id);
    }

    public List<Section> findSectionsByLineId(Long lineId){
        return jdbcTemplate.query(SectionQuery.SELECT_BY_LINE, sectionRowMapper, lineId);
    }

    public void deleteByStationId(Long lineId, Long stationId) {
        jdbcTemplate.update(SectionQuery.DELETE_BY_LINE_AND_STATIONID, lineId, stationId, stationId);
    }
}
