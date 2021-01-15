package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import subway.line.Section;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class StationDao {


    private JdbcTemplate jdbcTemplate;

    public StationDao(){
        jdbcTemplate=new JdbcTemplate();
    }
    private final RowMapper<Station> actorRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public void save(Station station) {
        String sql="insert into station (name) values (?)";
        jdbcTemplate.update(sql,station.getName());
    }

    public Station findById(Long id){
        String sql="select * from station where id=?";
        return jdbcTemplate.queryForObject(sql,new Long[]{id}, actorRowMapper);
    }
    public Station findByName(String name){
        System.out.println(name);
        return jdbcTemplate.queryForObject("Select * from station where name=?",	new Object[] {name},
                new RowMapper<Station>() {
                    public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Station station = new Station();
                        station.setId(rs.getLong("id"));
                        station.setName(rs.getString("name"));
                        return station;
                    }
                    } );

    }

    public boolean hasSameStationName(Station station){
        int cnt=jdbcTemplate.queryForObject("Select count(*) From station where name=?",Integer.class,station.getName());
        return cnt!=0;
    }


    public List<Station> findAll() {
        return jdbcTemplate.queryForList("select * from station",Station.class);
    }

    public void deleteById(Long id) {
       jdbcTemplate.update("delete from station where id=?",id);
    }


    public List<StationResponse> getStationResponseList(List<Station> station){
        return station
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }
}
