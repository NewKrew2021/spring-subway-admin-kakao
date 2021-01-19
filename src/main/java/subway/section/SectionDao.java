package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.exceptions.InvalidSectionException;

import java.util.List;

@Repository
public class SectionDao {

    private JdbcTemplate jdbcTemplate;

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

    public void save(Section section) {
        String sql = "insert into SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?,?,?,?)";
        try {
            jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
        } catch (Exception e) {
            throw new InvalidSectionException("구간 저장 오류가 발생했습니다.");
        }
    }

    public void updateById(Section section) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public int deleteAllByLineId(Long lineId) {
        return jdbcTemplate.update("delete from section where line_id = ?", lineId);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("delete from SECTION where id = ?", id);
    }

    public List<Section> findAllSectionsByLineId(Long lineId) {
        return jdbcTemplate.query("select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ?",
                sectionRowMapper, lineId);
    }

}
