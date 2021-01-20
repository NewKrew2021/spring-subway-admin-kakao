package subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Sections;

import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_SECTION="insert into section (line_id, up_station_id,down_station_id,distance) values(?,?,?,?)";
    private final String SELECT_BY_ID="select * from section where line_id=?";
    private final String UPDATE_SECTION= "update section set up_station_id = ?, down_station_id = ?,distance =? where id=?";
    private final String DELETE_SECTION="delete from section where id=?";

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
        jdbcTemplate.update(INSERT_SECTION, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Sections findByLineId(long lineId) {
        return new Sections(jdbcTemplate.query(SELECT_BY_ID, sectionRowMapper, lineId));
    }

    public void modify(Section section) {
        jdbcTemplate.update(
                UPDATE_SECTION, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public void delete(Long id) {
        jdbcTemplate.update(DELETE_SECTION, id);
    }


}
