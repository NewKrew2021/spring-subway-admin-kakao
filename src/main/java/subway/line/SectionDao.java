package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
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

    public Section save(Section section){
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?,?,?,?)";

        KeyHolder keyHoler = new GeneratedKeyHolder();

        jdbcTemplate.update(e -> {
            PreparedStatement preparedStatement = e.prepareStatement(
                    sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, section.getLineId());
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setLong(4, section.getDistance());
            return preparedStatement;
        }, keyHoler);

        Long id = (long) keyHoler.getKey();
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from section where id = ?", id);
    }

    public List<Section> findSectionsByLineId(Long lineId){
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }


    public Section findSectionByLineIdAndDownStationId(Long lineId, Long stationId) {
        String sql = "select * from section where line_id = ? and down_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, stationId);
    }

    public Section findSectionByLineIdAndUpStationId(Long lineId, Long stationId) {
        String sql = "select * from section where line_id = ? and up_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, stationId);
    }

    public int countByLineId(Long id) {
        String sql = "select count(*) from section where line_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }
}
