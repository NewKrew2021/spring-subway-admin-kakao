package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;

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

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public int update(Section section) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        return jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public Section insert(Section section) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(section);
        Number number = insertActor.executeAndReturnKey(sqlParameterSource);
        return new Section(number.longValue(), section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Section findByLineIdAndUpStationId(Long lineId, Long upStationId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ? and up_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, upStationId);
    }

    public Section findByLineIdAndDownStationId(Long lineId, Long downStationId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ? and down_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, downStationId);
    }

    public int delete(Section section) {
        String sql = "delete from section where id = ?";
        return jdbcTemplate.update(sql, section.getId());
    }

    public boolean existByLineIdAndUpStationId(Long lineId, Long upStationId) {
        String sql = "select exists(select * from section where line_id = ? and up_station_id = ?) as success";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, upStationId);
    }

    public boolean existByLineIdAndDownStationId(Long lineId, Long downStationId) {
        String sql = "select exists(select * from section where line_id = ? and down_station_id = ?) as success";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, downStationId);
    }

    public int countByLineId(Long lineId) {
        String sql = "select count(*) from section where line_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, lineId);
    }

}
