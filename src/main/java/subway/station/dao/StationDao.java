package subway.station.dao;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import subway.station.domain.Station;

import java.util.List;

@Repository
public class StationDao {

    private JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Station save(Station station) {
        String sql = "insert into station (name) values (?)";

        KeyHolder keyHoler = new org.springframework.jdbc.support.GeneratedKeyHolder();

        jdbcTemplate.update(e -> {
                    java.sql.PreparedStatement preparedStatement = e.prepareStatement(
                    sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
            }, keyHoler);

        Long id = (long) keyHoler.getKey();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(
                sql,
                (resultSet,rowNum)->{
                    Station station = new Station(
                            resultSet.getLong("id"),
                            resultSet.getString("name")
            );
            return station;
        });
    }

    public Station findById (Long id){
        String sql = "select * from station where id = ?";
        return jdbcTemplate.queryForObject(
                sql,
                (resultSet, rowNum) -> {
                    Station station = new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                    );
                    return station;
                }, id);
    }

    public void deleteById(Long id) {
        String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql,id);
    }

}
