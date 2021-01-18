package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@Transactional
public class SectionDao {
    private static final RowMapper<Section> sectionMapper = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance")
    );

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSection(section), keyHolder);
        Long id = (Long) keyHolder.getKey();
        return new Section(
                id,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }

    private PreparedStatementCreator insertSection(Section section) {
        return con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            psmt.setLong(1, section.getLineId());
            psmt.setLong(2, section.getUpStationId());
            psmt.setLong(3, section.getDownStationId());
            psmt.setInt(4, section.getDistance());
            return psmt;
        };
    }

    @Transactional(readOnly = true)
    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query("select * from section where line_id = ?", sectionMapper, lineId);
    }

    public void delete(Section section) {
        jdbcTemplate.update("delete from section where id = ?", section.getId());
    }
}
