package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.line.LineRequest;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insertOnCreateLine(Long lineId, LineRequest request) {

        Section upSection = new Section(lineId, request.getUpStationId(), 0);
        Section downSection = new Section(lineId, request.getDownStationId(), request.getDistance());

        if (upSection.equals(downSection)) {
            return false;
        }

        insertSection(upSection);
        insertSection(downSection);

        return true;
    }

    public boolean insert(Long lineId, SectionRequest request) {
        Sections sections = findByLineId(lineId);

        Section newSection = sections.insert(request);
        if (newSection == null) {
            return false;
        }

        insertSection(newSection);
        return true;
    }

    public boolean delete(Section section) {
        Sections sections = findByLineId(section.getLineId());
        if (sections.hasOnlyTwoSections()) {
            return false;
        }

        deleteSection(section);
        return true;
    }

    private boolean insertSection(Section section) {
        String sql = "insert into section (line_id, station_id, distance) values(?, ?, ?)";
        return jdbcTemplate.update(sql, section.getLineId(), section.getStationId(), section.getDistance()) > 0;
    }

    private boolean deleteSection(Section section) {
        String sql = "delete from section where line_id = ? and station_id = ?";
        return jdbcTemplate.update(sql, section.getLineId(), section.getStationId()) > 0;
    }

    public Sections findByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineId));
    }

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("station_id"),
                    resultSet.getInt("distance"));
}
