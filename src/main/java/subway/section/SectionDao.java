package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            psmt.setLong(1, section.getLineId());
            psmt.setLong(2, section.getUpStationId());
            psmt.setLong(3, section.getDownStationId());
            psmt.setInt(4, section.getDistance());
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();
        return new Section(
                id,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }

    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query("select * from section where line_id = ?", new SectionMapper(), lineId);
    }

    public void delete(Section section) {
        jdbcTemplate.update("delete from section where id = ?", section.getId());
    }

    private final static class SectionMapper implements RowMapper<Section> {
        @Override
        public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance")
            );
        }
    }
}
