package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> actorRowMapper = (resultSet, rowNum) -> Section.of(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    public Section save(Section section) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Section.of(keyHolder.getKey().longValue(), section);
    }

    public void update(Long originSectionId, Section newSection) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance(), originSectionId);
    }

    public List<Section> getSectionsByLineId(Long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, actorRowMapper, lineId);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from SECTION where id = ?", id);
    }
}
