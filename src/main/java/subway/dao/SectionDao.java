package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.DeleteImpossibleException;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final StationDao stationDao;

    public SectionDao(JdbcTemplate jdbcTemplate, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationDao = stationDao;
    }

    public boolean existSection(Section section) {
        String sql = "select count(*) from section where up_station_id = ? and down_station_id = ? and line_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, section.getUpStationId(), section.getDownStationId(), section.getLineId()) > 0;
    }

    public Section save(Section section) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("section")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getLineId());
    }

    public Sections getSectionsByLineId(Long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        Sections sections = new Sections(jdbcTemplate.query(sql, (rs, rowNum) -> new Section(rs.getLong("id"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                rs.getInt("distance"),
                rs.getLong("line_id")), lineId));
        List<Long> ids = sections.getStationIds();
        sections = new Sections(sections.getSections(), ids.stream().map(id -> {
            Station station = stationDao.findOne(id);
            return new Station(station.getId(), station.getName());
        }).collect(Collectors.toList()));
        return sections;
    }

    public void deleteSectionById(Long sectionId) {
        String sql = "delete from SECTION where id = ?";
        if (jdbcTemplate.update(sql, sectionId) == 0) {
            throw new DeleteImpossibleException();
        }
    }

    public void deleteSectionByLineId(Long lineId) {
        String sql = "delete from SECTION where line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
