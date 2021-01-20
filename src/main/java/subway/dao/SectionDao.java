package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.section.Section;

import javax.sql.DataSource;
import java.util.List;

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
                resultSet.getLong("station_id"),
                resultSet.getInt("distance"),
                resultSet.getLong("line_id")
        );
        return section;
    };

    public Section save(Section section) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getStationId(), section.getDistance(), section.getLineId());
    }

    public List<Section> findAllStationsByLineId(long lineId) {
        String sql = "select * from section where line_id = ? order by distance";
        return jdbcTemplate.query(sql, actorRowMapper, lineId);
    }

    public int deleteByStationId(long stationId) {
        String sql = "delete from section where station_id = ?";
        return jdbcTemplate.update(sql, stationId);
    }
}
