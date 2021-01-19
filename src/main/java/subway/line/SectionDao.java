package subway.line;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insert(Long lineId, Section section) {
        Sections sections = findByLineId(lineId);
        if (isNewLine(sections)) {
            insertInNewLine(lineId, section);
            return true;
        }

        Section upSection = findByStationId(section.getLineId(), section.getUpStationId());
        Section downSection = findByStationId(section.getLineId(), section.getDownStationId());
        if (notInsertableSections(upSection, downSection)) {
            return false;
        }

        if (isNewSection(upSection) && findByDownStationId(lineId, section.getUpStationId()) == null) {
            Section newSection = new Section(lineId,
                    section.getUpStationId(),
                    section.getDownStationId(),
                    section.getDistance());
            insertSection(newSection);

            return true;
        }

        if (isNewSection(downSection) && findByUpStationId(lineId, section.getDownStationId()) == null) {
            Section newSection = new Section(lineId,
                    section.getUpStationId(),
                    section.getDownStationId(),
                    section.getDistance());
            insertSection(newSection);

            return true;
        }

        Section firstSection, secondSection;
        if (!isNewSection(upSection)) {
            try {
                firstSection = new Section(upSection.getId(), lineId,
                        upSection.getUpStationId(),
                        section.getDownStationId(),
                        section.getDistance());

                secondSection = new Section(lineId,
                        section.getDownStationId(),
                        upSection.getDownStationId(),
                        upSection.getDistance() - section.getDistance());
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        } else {
            try {
                firstSection = new Section(downSection.getId(), lineId,
                        downSection.getUpStationId(),
                        section.getUpStationId(),
                        downSection.getDistance() - section.getDistance());

                secondSection = new Section(lineId,
                        section.getUpStationId(),
                        downSection.getDownStationId(),
                        section.getDistance());
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }

        updateSection(firstSection);
        insertSection(secondSection);

        return true;
    }

    public boolean delete(Long lineId, Long stationId) {
        Sections sections = findByLineId(lineId);
        if (sections.hasOnlyOne()) {
            return false;
        }

        Section upSection = findByDownStationId(lineId, stationId);
        Section downSection = findByUpStationId(lineId, stationId);

        if (neitherExists(upSection, downSection)) {
            return false;
        }

        if (bothExists(upSection, downSection)) {
            int distance = upSection.getDistance() + downSection.getDistance();
            Section section = new Section(upSection.getId(), lineId,
                    upSection.getUpStationId(), downSection.getDownStationId(), distance);

            updateSection(section);
            deleteSection(downSection);
            return true;
        }

        deleteSection(isNewSection(upSection) ? downSection : upSection);
        return true;
    }

    public Sections findByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineId));
    }

    public Section findByUpStationId(Long lineId, Long upStationId) {
        String sql = "select * from section where up_station_id = ? and line_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, sectionRowMapper, upStationId, lineId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    private boolean isNewLine(Sections sections) {
        return sections.empty();
    }

    private void insertInNewLine(Long lineId, Section section) {
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, Section.TERMINAL_ID, section.getUpStationId(), 0);

        jdbcTemplate.update(sql,
                lineId,
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());

        jdbcTemplate.update(sql, lineId, section.getDownStationId(), Section.TERMINAL_ID, 0);
    }

    private boolean insertSection(Section section) {
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()) > 0;
    }

    private boolean updateSection(Section section) {
        String sql = "update section set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        return jdbcTemplate.update(sql,
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                section.getId()) > 0;
    }

    private boolean deleteSection(Section section) {
        String sql = "delete from section where id = ?";
        return jdbcTemplate.update(sql, section.getId()) > 0;
    }

    private Section findByStationId(Long lineId, Long stationId) {
        Section upSection = findByUpStationId(lineId, stationId);
        Section downSection = findByDownStationId(lineId, stationId);

        return isNewSection(upSection) ? downSection : upSection;
    }

    private Section findByDownStationId(Long lineId, Long downStationId) {
        String sql = "select * from section where down_station_id = ? and line_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, sectionRowMapper, downStationId, lineId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    private boolean notInsertableSections(Section upSection, Section downSection) {
        return neitherExists(upSection, downSection) || bothExists(upSection, downSection);
    }

    private boolean bothExists(Section upSection, Section downSection) {
        return !isNewSection(upSection) && !isNewSection(downSection);
    }

    private boolean neitherExists(Section upSection2, Section downSection2) {
        return isNewSection(upSection2) && isNewSection(downSection2);
    }

    private boolean isNewSection(Section upSection) {
        return upSection == null;
    }

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance"));
}
