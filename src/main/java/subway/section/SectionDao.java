package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
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
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, section.getLineId());
            pstmt.setLong(2, section.getUpStationId());
            pstmt.setLong(3, section.getDownStationId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        }, keyHolder);

        return Section.of(keyHolder.getKey().longValue(), section);
    }

    public void update(Long originSectionId, Section newSection) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance(), originSectionId);
    }

    public List<Section> getSectionsByLineId(Long lineId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ?";

        return jdbcTemplate.query(sql, actorRowMapper, lineId);
    }

    public int deleteById(Long id) {
        String sql = "delete from SECTION where id = ?";

        return jdbcTemplate.update(sql, id);
    }
}
