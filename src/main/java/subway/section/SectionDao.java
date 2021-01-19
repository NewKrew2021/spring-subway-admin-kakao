package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SectionDao {

    final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Section section) {
        String SQL = "INSERT INTO section (line_id, station_id, distance, next_id ) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(SQL, section.getLineId(), section.getStationId(), section.getDistance(), section.getNextStationId());
    }

    public List<Section> getSectionsOfLine(Long id) {
        String SQL = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(
                SQL, new SelectSectionMapper(), id);
    }

    public Section getSection(Long stationId, Long lineId) {
        String SQL = "SELECT * FROM section WHERE station_id = ? AND line_id = ?";
        List<Section> sections = jdbcTemplate.query(
                SQL, new SelectSectionMapper(), stationId, lineId);
        return sections.isEmpty() ? Section.DO_NOT_EXIST_SECTION : sections.get(0);
    }

    public Section getSectionByNextId(Long nextId) {
        if( nextId == null ) {
            return null;
        }
        String SQL = "SELECT * FROM section WHERE next_id = ?";
        List<Section> sections = jdbcTemplate.query( SQL, new SelectSectionMapper(), nextId);
        return sections.isEmpty() ? Section.DO_NOT_EXIST_SECTION : sections.get(0);
    }

    public void update(Section prevSection) {
        if(  prevSection == Section.DO_NOT_EXIST_SECTION) {
            return;
        }
        String SQL = "UPDATE section SET next_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(SQL, prevSection.getNextStationId(), prevSection.getDistance(), prevSection.getId());
    }

    public int countOfSections(Long lineId) {
        String SQL = "SELECT count(*) FROM section WHERE line_id = ?";
        return jdbcTemplate.queryForObject(SQL, Integer.class, lineId);
    }

    public void delete(Section section) {
        String SQL = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(SQL, section.getId());
    }

    private final static class SelectSectionMapper implements RowMapper<Section> {
        @Override
        public Section mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("station_id"),
                    resultSet.getInt("distance"),
                    resultSet.getLong("next_id")
            );
        }
    }

}
