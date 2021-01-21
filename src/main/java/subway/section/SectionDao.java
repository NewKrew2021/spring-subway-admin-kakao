package subway.section;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.section.domain.Section;
import subway.section.domain.Sections;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section insert(Section section) {
        String sql = "insert into section (line_id, station_id, distance) values(?, ?, ?)";

        try {
            jdbcTemplate.update(sql, section.getLineID(), section.getStationID(), section.getDistance());
        } catch (DataAccessException ignored) {
            return null;
        }

        return new Section(section.getLineID(), section.getStationID(), section.getDistance());
    }

    public Section delete(Section section) {
        String sql = "delete from section where line_id = ? and station_id = ?";
        int affectedRows = jdbcTemplate.update(sql, section.getLineID(), section.getStationID());

        if (isNotDeleted(affectedRows)) {
            return section;
        }

        return null;
    }

    public Sections findAllSectionsOf(Long lineID) {
        String sql = "select * from section where line_id = ? order by distance";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineID));
    }

    private boolean isNotDeleted(int affectedRows) {
        return noRowsWereAffected(affectedRows);
    }

    private boolean noRowsWereAffected(int affectedRows) {
        return affectedRows != 1;
    }

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("line_id"),
                    resultSet.getLong("station_id"),
                    resultSet.getInt("distance"));
}
