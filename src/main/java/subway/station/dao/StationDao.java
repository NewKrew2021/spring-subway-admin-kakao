package subway.station.dao;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import subway.station.domain.Station;
import subway.station.query.StationQuery;

import java.util.List;

@Repository
public class StationDao {

    private JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Station save(String name) {

        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();

        jdbcTemplate.update(e -> {
                    java.sql.PreparedStatement preparedStatement = e.prepareStatement(
                            StationQuery.INSERT, java.sql.Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            return preparedStatement;
            }, keyHolder);

        Long id = (long) keyHolder.getKey();
        return new Station(id, name);
    }

    public List<Station> findAll() {
        return jdbcTemplate.query(
                StationQuery.SELECT_ALL,
                (resultSet,rowNum)->{
                    Station station = new Station(
                            resultSet.getLong("id"),
                            resultSet.getString("name")
            );
            return station;
        });
    }

    public Station findById (Long id){
        return jdbcTemplate.queryForObject(
                StationQuery.SELECT_BY_ID,
                (resultSet, rowNum) -> {
                    Station station = new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                    );
                    return station;
                }, id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(StationQuery.DELETE_BY_ID,id);
    }

    public int countByName(String name) {
        return jdbcTemplate.queryForObject(StationQuery.COUNT_BY_NAME, Integer.class, name);
    }

}
