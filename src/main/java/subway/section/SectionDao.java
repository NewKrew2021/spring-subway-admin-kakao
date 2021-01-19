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
    public static final int INITIAL_DISTANCE = 0;

    private final static RowMapper<Section> sectionMapper = ((rs, rowNum) ->
            new Section(rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("station_id"),
                    rs.getInt("distance")));

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into section (line_id,station_id,distance) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            psmt.setLong(1, section.getLineId());
            psmt.setLong(2, section.getStationId());
            psmt.setInt(3, section.getDistance());
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();
        return new Section(section.getLineId(), section.getStationId(), section.getDistance());
    }

    public List<Section> findAll() {
        return jdbcTemplate.query("select * from section ", sectionMapper);
    }

    public List<Section> findByLineId(Long lineId) {
        return jdbcTemplate.query("select * from section where line_id = ?", sectionMapper, lineId);
    }

    public void makeSection(Long upStationId, Long downStationId, int distance, Long lineId) {
        Sections sectionsByLineId = new Sections(findByLineId(lineId));
        sectionsByLineId.validateSection(upStationId, downStationId, distance);
        save(new Section(lineId,
                sectionsByLineId.getExtendedStationId(upStationId, downStationId),
                sectionsByLineId.calculateRelativeDistance(upStationId, downStationId, distance)));
    }

    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        Sections sectionsByLineId = new Sections(findByLineId(lineId));
        sectionsByLineId.validateDeleteStation(stationId);
        jdbcTemplate.update("delete from section where line_id = ? and station_id = ?", lineId, stationId);
    }

    public void LineInitialize(Long lineId, Long upStationId, Long downStationId, int distance) {
        save(new Section(lineId, upStationId, INITIAL_DISTANCE));
        save(new Section(lineId, downStationId, distance));
    }

}
