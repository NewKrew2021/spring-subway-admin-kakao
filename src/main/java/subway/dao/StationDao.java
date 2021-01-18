package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Station;
import subway.query.Sql;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;
    private final SimpleJdbcInsert insertActor;
    private final RowMapper<Station> stationMapper = (rs, rowNum) ->
            new Station(rs.getLong(1), rs.getString(2));

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate parameterJdbcTemplate,
                      DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.parameterJdbcTemplate = parameterJdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(Station station) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        Long stationId = insertActor.executeAndReturnKey(parameters).longValue();
        return new Station(stationId, station.getName());
    }

    public Station getById(Long stationId) {
        return this.jdbcTemplate.queryForObject(
                Sql.SELECT_STATION_WITH_ID,
                stationMapper,
                stationId);
    }

    public List<Station> batchGetByIds(List<Long> stationIds) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", stationIds);
        return parameterJdbcTemplate.query(
                Sql.BATCH_SELECT_FROM_STATION,
                parameters,
                stationMapper
        );
    }

    public List<Station> findAll() {
        return this.jdbcTemplate.query(
                Sql.SELECT_ALL_STATIONS,
                stationMapper
        );
    }

    public boolean deleteById(Long stationId) {
        return this.jdbcTemplate.update(Sql.DELETE_STATION_WITH_ID, stationId) > 0;
    }
}
