package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.section.domain.Section;
import subway.section.domain.Sections;

import java.util.NoSuchElementException;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section insert(Section section) {
        String sql = "insert into section (line_id, station_id, distance) values(?, ?, ?)";
        jdbcTemplate.update(sql, section.getLineID(), section.getStationID(), section.getDistance());

        return new Section(section.getLineID(), section.getStationID(), section.getDistance());
    }

    public void delete(Section section) {
        String sql = "delete from section where line_id = ? and station_id = ?";
        int affectedRows = jdbcTemplate.update(sql, section.getLineID(), section.getStationID());

        if (isNotDeleted(affectedRows)) {
            throw new NoSuchElementException(
                    String.format("Could not delete section with line id: %d and station id: %d",
                            section.getLineID(), section.getStationID()));
        }
    }

    public Sections findAllSectionsOf(Long lineID) {
        String sql = "select * from section where line_id = ? order by distance";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineID));
    }

    public Section findOneBy(long lineID, long sectionID) {
        String sql = "select * from section where line_id = ? and station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, lineID, sectionID);
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
