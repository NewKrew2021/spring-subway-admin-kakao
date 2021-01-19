package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exception.NoContentException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

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

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public void saveAll(SectionGroup sections) {
        sections.getSections().stream()
                .forEach(section -> {
                    jdbcTemplate.update("insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)",
                            section.getLineId(),
                            section.getUpStationId(),
                            section.getDownStationId(),
                            section.getDistance());
                });
    }

    public List<Section> findAllByLineId(Long lineId) {
        return jdbcTemplate.query("select * from section where line_id = ?", sectionRowMapper, lineId);
    }

    public void update(Section section) {
        int updateResult = jdbcTemplate.update("update section set up_station_id = ?, down_station_id = ?, distance = ? where id = ?",
                section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
        if (updateResult == 0) {
            throw new NoContentException("해당 id를 갖는 구간이 존재하지 않습니다.");
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from section where id = ?", id);
    }

}
