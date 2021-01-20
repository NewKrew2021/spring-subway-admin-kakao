package subway.section.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.section.domain.Section;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> actorRowMapper = (resultSet, rowNum) -> Section.of(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SectionSql.INSERT.getSql(), Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Section.of(keyHolder.getKey().longValue(), section);
    }

    public void update(Long originSectionId, Section newSection) {
        jdbcTemplate.update(SectionSql.UPDATE.getSql(), newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance(), originSectionId);
    }

    public List<Section> getSectionsByLineId(Long lineId) {
        return jdbcTemplate.query(SectionSql.SELECT_BY_LINE_ID.getSql(), actorRowMapper, lineId);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(SectionSql.DELETE_BY_ID.getSql(), id);
    }

    public void deleteByLineId(Long lineId) {
        jdbcTemplate.update(SectionSql.DELETE_BY_LINE_ID.getSql(), lineId);
    }
}
