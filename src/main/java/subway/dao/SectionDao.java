package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Sections;

@Repository
public class SectionDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public void save(Section section) {
        String sql = "insert into section (line_id, up_station_id,down_station_id,distance) values(?,?,?,?)";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Sections findSectionsByLineId(long lineId) {
        String sql = "select * from section where line_id=?";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineId));
    }

    public void update(Section section) {
        String sql = "update section set up_station_id = ?, down_station_id = ?,distance =? where id=?";
        jdbcTemplate.update(
                sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public void delete(Long id) {
        String sql = "delete from section where id=?";
        jdbcTemplate.update(sql, id);
    }

    public int count(){
        String sql = "select count(*) from section";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


}
