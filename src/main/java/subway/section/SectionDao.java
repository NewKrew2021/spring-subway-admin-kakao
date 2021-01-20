package subway.section;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Section upSection, Section downSection) {
        if (upSection.equals(downSection)) {
            throw new IllegalArgumentException("UpSection and DownSection cannot be equal");
        }

        Sections sections = findAllSectionsOf(upSection.getLineID());

        if (sections.hasNoSections()) {
            insertSection(upSection);
            insertSection(downSection);
            return;
        }

        insertSection(sections.insert(upSection, downSection));
    }

    public void delete(Section section) {
        Sections sections = findAllSectionsOf(section.getLineID());
        if (sections.hasMinimumSectionCount()) {
            throw new IllegalArgumentException("Cannot delete section when there are only two sections left");
        }

        deleteSection(section);
    }

    public Sections findAllSectionsOf(Long lineID) {
        String sql = "select * from section where line_id = ? order by distance";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineID));
    }

    private Section insertSection(Section section) {
        String sql = "insert into section (line_id, station_id, distance) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement st = con.prepareStatement(sql, new String[]{"id"});
                st.setLong(1, section.getLineID());
                st.setLong(2, section.getStationID());
                st.setInt(3, section.getDistance());
                return st;
            }, keyHolder);
        } catch (DataAccessException ignored) {
            throw new IllegalArgumentException(
                    String.format("Cannot create section. Station %d in line %d already exists",
                            section.getStationID(), section.getLineID()));
        }

        return new Section(keyHolder.getKey().longValue(), section.getLineID(),
                section.getStationID(), section.getDistance());
    }

    private void deleteSection(Section section) {
        String sql = "delete from section where line_id = ? and station_id = ?";
        int affectedRows = jdbcTemplate.update(sql, section.getLineID(), section.getStationID());

        if (!affectedToUniqueRowOnly(affectedRows)) {
            throw new IllegalArgumentException(
                    String.format("Could not delete section with station id: %d and line id: %d",
                            section.getStationID(), section.getLineID()));
        }
    }

    private boolean affectedToUniqueRowOnly(int affectedRows) {
        return affectedRows == 1;
    }

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("station_id"),
                    resultSet.getInt("distance"));
}
