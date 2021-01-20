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
                    "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)",
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
                section.getDistance()
        );
    }

    public void deleteById(Long id) {
        String query = "delete from section where id = ?";
        jdbcTemplate.update(query, id);
    }

    public List<Section> findAllByLineId(Long id){
        String query = "select * from section where line_id = ?";
        return jdbcTemplate.query(query, new SectionMapper(), id);
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, upstationId가 일치하는 section을 return
     */
    public Section getSectionByUpStationId(Long lineId, Long upStationId) {
        try {
            String sqlQuery = "select * from section where line_id = ? and up_station_id = ? limit 1";
            return jdbcTemplate.queryForObject(sqlQuery, new SectionMapper(), lineId, upStationId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, downStationId가 일치하는 section을 return
     */
    public Section getSectionByDownStationId(Long lineId, Long downStationId) {
        try {
            String sqlQuery = "select * from section where line_id = ? and down_station_id = ? limit 1";
            return jdbcTemplate.queryForObject(sqlQuery, new SectionMapper(), lineId, downStationId);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean alreadyExistInLine(Long lineId, Long stationId) {
        String sqlQuery = "select count(*) from section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        int existCount = jdbcTemplate.queryForObject(sqlQuery, int.class, lineId, stationId, stationId);
        return existCount != 0;
    }

    private final static class SectionMapper implements RowMapper<Section> {
        @Override
        public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId= rs.getLong("down_station_id");
            int distance = rs.getInt("distance");

            return new Section(id, lineId, upStationId, downStationId, distance);
        }
    }
}