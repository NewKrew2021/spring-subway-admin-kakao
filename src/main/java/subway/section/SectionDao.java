package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SectionDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
        return section;
    };

    public void save(Section section){
        String sql = "insert into section (line_id, up_station_id,down_station_id,distance) values(?,?,?,?)";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public List<Section> findSectionsByLineId(long lineId){
        String sql="select * from section where line_id=?";
        return jdbcTemplate.query(sql,sectionRowMapper,lineId);
    }

    public void modifySection(Section section){
        String sql = "update section set up_station_id = ?, down_station_id = ?,distance =? where id=?";
        jdbcTemplate.update(
                sql,section.getUpStationId(),section.getDownStationId(),section.getDistance(),section.getId());
    }
    public void deleteSection(Long id){
        String sql="delete from section where id=?";
        jdbcTemplate.update(sql,id);
    }



}
