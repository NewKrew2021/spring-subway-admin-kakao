package subway.section;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Section> actorRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance"),
                resultSet.getLong("line_id")
        );
        return section;
    };

    public Section save(Section section) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getLineId());
    }

    public Section findByUpStationId(long id) {
        String sql = "select id, up_station_id, down_station_id, distance, line_id from section where up_station_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, actorRowMapper, upstationId, lineId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public Section findByDownStationId(long id) {
        String sql = "select id, up_station_id, down_station_id, distance, line_id from section where down_station_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, actorRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int updateSection(long id, Section section) {
        String sql = "update section set up_station_id = ?, down_station_id = ? , distance = ?, line_id = ? where id = ?";
        return jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getLineId() , id);
    }

    public int deleteById(long id) {
        String sql = "delete from section where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
