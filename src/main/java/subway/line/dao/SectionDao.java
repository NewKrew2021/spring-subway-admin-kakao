package subway.line.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.line.domain.Section;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Section findByUpStationId(Long upStationId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where up_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, upStationId);
    }

    public Section findByDownStationId(Long downStationId) {
        String sql = "select line_id, up_station_id, down_station_id, distance from SECTION where down_station_id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, downStationId);
    }

    public int delete(Section section) {
        String sql = "delete from section where id = ? and up_station_id = ? and down_station_id = ?";
        return jdbcTemplate.update(sql, section.getId(), section.getUpStationId(), section.getDownStationId());
    }

    public int deleteByLineId(Long lineId) {
        String sql = "delete from section where line_id = ?";
        return jdbcTemplate.update(sql, lineId);
    }

    public List<Section> showAllByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public void saveAll(List<Section> sections) {
        List<Map<String, Object>> batchValues = sections.stream()
                .map(section -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("line_id", section.getLineId());
                    params.put("up_station_id", section.getUpStationId());
                    params.put("down_station_id", section.getDownStationId());
                    params.put("distance", section.getDistance());
                    return params;
                })
                .collect(Collectors.toList());

        insertActor.executeBatch(batchValues.toArray(new Map[sections.size()]));
    }

}
