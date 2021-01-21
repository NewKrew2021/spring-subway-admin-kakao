package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance"));

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, section.getLineId());
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setLong(4, section.getDistance().getDistance());
            return preparedStatement;
        }, keyHolder);

        Long id = (long) keyHolder.getKey();
        return new Section(id,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }

    public void delete(Long sectionId) {
        jdbcTemplate.update("delete from section where id = ?", sectionId);
    }

    public void deleteSections(Sections sections) {
        List<Long> sectionIds = sections.getSectionIds();
        for (Long sectionId : sectionIds) {
            jdbcTemplate.update("delete from section where id = ?", sectionId);
        }
    }

    public Sections findSectionsByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineId));
    }

    public NamedSections findNamedSectionByLineId(Long lineId) {
        String sql = "select * from section as sc " +
                "left outer join station as st on sc.down_station_id = st.id " +
                "where line_id = ?";
        return new NamedSections(jdbcTemplate.query(sql, (resultSet, rowNum) -> new NamedSection(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance"),
                resultSet.getString("name")), lineId));
    }

    public Sections findJointSections(Long lineId, Long stationId) {
        String sql = "select * from section where line_id = ? AND up_station_id = ? OR down_station_id = ?";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineId, stationId, stationId));
    }

    public Section findSectionByUpStationId(Long lineId, Long upStationId) {
        String sql = "select * from section where line_id = ? AND up_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, upStationId);
    }

    public Section findSectionByDownStationId(Long lineId, Long downStationId) {
        String sql = "select * from section where line_id = ? AND down_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, downStationId);
    }

    public int countByUpStationId(Long lineId, Long stationId) {
        String sql = "select count(*) from section where line_id = ? AND up_station_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, lineId, stationId);
    }

}
