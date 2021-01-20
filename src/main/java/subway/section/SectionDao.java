package subway.section;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exception.exceptions.EmptySectionException;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {

    private static final String EMPTY_SECTION_MESSAGE = "라인 내에 구간이 존재하지 않습니다.";

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
        return section;
    };

    public long save(long lineId, SectionRequest sectionRequest) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(
                    "insert into SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)",
                    new String[] {"id"}
                    );
            pstmt.setLong(1, lineId);
            pstmt.setLong(2, sectionRequest.getUpStationId());
            pstmt.setLong(3, sectionRequest.getDownStationId());
            pstmt.setLong(4, sectionRequest.getDistance());
            return pstmt;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Section> findByLineId(long id) {
        try {
            return jdbcTemplate.query("select * from SECTION where line_id = ?", sectionRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptySectionException(EMPTY_SECTION_MESSAGE+" : "+e.getMessage());
        }
    }

    public void updateSection(Section section) {
        jdbcTemplate.update(
                "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?",
                section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId()
        );
    }

    public void deleteByLineIdAndUpStationId(long lineId, long stationId) {
        jdbcTemplate.update("delete from SECTION where line_id = ? and up_station_id = ?", lineId, stationId);
    }

    public void deleteByLineIdAndDownStationId(long lineId, long stationId) {
        jdbcTemplate.update("delete from SECTION where line_id = ? and down_station_id = ?", lineId, stationId);
    }

    public int deleteAllByLineId(long id) {
        return jdbcTemplate.update("delete from SECTION where line_id = ?", id);
    }
}
