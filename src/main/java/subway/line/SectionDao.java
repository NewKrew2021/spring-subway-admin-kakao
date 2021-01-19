package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insert(Section upSection, Section downSection) {
        if (upSection.equals(downSection)) {
            return false;
        }

        Sections sections = findAllSectionsOf(upSection.getLineID());

        if (sections.hasNoSections()) {
            insertSection(upSection);
            insertSection(downSection);
            return true;
        }

        Section newSection = sections.insert(upSection, downSection);
        if (couldNotInsertNewSection(newSection)) {
            return false;
        }

        insertSection(newSection);
        return true;
    }

    public boolean delete(Section section) {
        Sections sections = findAllSectionsOf(section.getLineID());
        if (sections.hasMinimumSectionCount()) {
            return false;
        }

        deleteSection(section);
        return true;
    }

    public Sections findAllSectionsOf(Long lineID) {
        String sql = "select * from section where line_id = ? order by distance";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineID));
    }

    private boolean insertSection(Section section) {
        String sql = "insert into section (line_id, station_id, distance) values(?, ?, ?)";
        return jdbcTemplate.update(sql, section.getLineID(), section.getStationID(), section.getDistance()) > 0;
    }

    private boolean deleteSection(Section section) {
        String sql = "delete from section where line_id = ? and station_id = ?";
        return jdbcTemplate.update(sql, section.getLineID(), section.getStationID()) > 0;
    }

    private boolean couldNotInsertNewSection(Section newSection) {
        return newSection == null;
    }

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("station_id"),
                    resultSet.getInt("distance"));
}
