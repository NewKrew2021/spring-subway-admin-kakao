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

    private final static RowMapper<Section> sectionMapper = ((rs, rowNum) ->
            new Section(rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("station_id"),
                    rs.getInt("relative_distance")));

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Long lineId, Long stationId, int relativeDistance) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into section (line_id,station_id,relative_distance) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            psmt.setLong(1, lineId);
            psmt.setLong(2, stationId);
            psmt.setInt(3, relativeDistance);
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();
        return new Section(lineId, stationId, relativeDistance);
    }

    public List<Section> findAll() {
        return jdbcTemplate.query("select * from section ", sectionMapper);
    }

    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query("select * from section where line_id = ?", sectionMapper, lineId);
    }

    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        jdbcTemplate.update("delete from section where line_id = ? and station_id = ?", lineId, stationId);
    }

}
