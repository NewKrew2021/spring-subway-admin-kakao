package subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.line.domain.Line;
import subway.line.dto.LineRequest;
import subway.section.domain.Section;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> rowMapper = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("station_id"),
            rs.getLong("line_id"),
            rs.getInt("position")
    );

    public Section save(long stationId, long lineId, int position) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSection(stationId, lineId, position), keyHolder);
        return findById((Long) keyHolder.getKey());
    }

    public Section save(Section section) {
        return save(section.getStationId(), section.getLineId(), section.getPosition());
    }

    private PreparedStatementCreator insertSection(long stationId, long lineId, int position) {
        return con -> {
            PreparedStatement psmt = con.prepareStatement("INSERT INTO section(station_id,line_id,position) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            psmt.setLong(1, stationId);
            psmt.setLong(2, lineId);
            psmt.setInt(3, position);
            return psmt;
        };
    }


    public void createLineSection(Line newLine, LineRequest lineRequest) {
        save(lineRequest.getUpStationId(), newLine.getId(), 0);
        save(lineRequest.getDownStationId(), newLine.getId(), lineRequest.getDistance());
    }

    public List<Section> getSections(long lineId) {
        String SQL = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(SQL, rowMapper, lineId);
    }

    public Section findById(long id) {
        String SQL = "SELECT id, line_id, station_id, position FROM section where id = ?";
        return jdbcTemplate.queryForObject(SQL, rowMapper, id);
    }


    public void delete(long lineId, long stationId) {
        String SQL = "DELETE FROM section WHERE line_id = ? AND station_id = ?";
        jdbcTemplate.update(SQL, lineId, stationId);
    }
}
