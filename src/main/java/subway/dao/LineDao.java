package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.dto.Line;
import subway.dto.LineRequest;

import java.util.List;

@Repository
public class LineDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    StationDao stationDao;

    private RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id")
        );
        return line;
    };

    public int save(Line line){
        String sql="insert into line (name,color,up_station_id,down_station_id) values (?,?,?,?)";
        return jdbcTemplate.update(sql,line.getName(),line.getColor(),line.getUpStationId(),line.getDownStationId());

    }

    public Line findLineByName(String name){
        String sql="select * from line where name=?";
        return jdbcTemplate.queryForObject(sql,lineRowMapper,name);

    }

    public boolean isContainSameName(String name){
        String sql = "select count(*) from line where name = ?";
        int count = jdbcTemplate.queryForObject(sql,Integer.class,name);
        return count != 0;
    }

    public Line findById(Long lineId){
        String sql = "select * from line where id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, lineId);
    }

    public List<Line> findAll(){
        String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public void updateLine(Line line){
        String sql = "update line set name=?, color = ?, up_station_id = ?, down_station_id = ?  where id=?";
        jdbcTemplate.update(
                sql,line.getName(),line.getColor(),line.getUpStationId(),line.getDownStationId(),line.getId());
    }
    public void modifyLineStationId(Line line){
        String sql = "update line set up_station_id = ?, down_station_id = ? where id=?";
        jdbcTemplate.update(
                sql,line.getUpStationId(),line.getDownStationId(),line.getId());
    }

    public void modifyLineUpStationId(Long id,Long upStationId){
        String sql = "update line set up_station_id = ? where id=?";
        jdbcTemplate.update(
                sql,upStationId,id);
    }
    public void modifyLineDownStationId(Long id,Long downStationId){
        String sql = "update line set down_station_id = ? where id=?";
        jdbcTemplate.update(
                sql,downStationId,id);
    }

    public void delete(Long id){
        String sql="delete from line where id=?";
        jdbcTemplate.update(sql,id);

    }
}
