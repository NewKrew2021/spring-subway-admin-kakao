package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SectionDao {
    private static final String UPDATE_BY_ID_SQL = "update section " +
            "set up_station_id = ?, down_station_id = ? , distance = ?, line_id = ? where id = ?";
    private static final String DELETE_BY_ID_SQL = "delete from section where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Section> actorRowMapper = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance"),
            resultSet.getLong("line_id")
    );

    public Section save(Section section) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getLineId());
    }

    public int updateById(long id, Section section) {
        return jdbcTemplate.update(UPDATE_BY_ID_SQL, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getLineId(), id);
    }

    public int deleteById(long id) {
        return jdbcTemplate.update(DELETE_BY_ID_SQL, id);
    }
}
