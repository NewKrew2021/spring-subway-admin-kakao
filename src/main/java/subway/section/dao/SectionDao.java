package subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.section.domain.Section;
import subway.section.domain.Sections;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section insert(Long lineId, Long stationId, int distance) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement st = con.prepareStatement(SectionDaoQuery.INSERT, new String[]{"id"});
            st.setLong(1, lineId);
            st.setLong(2, stationId);
            st.setInt(3, distance);
            return st;
        }, keyHolder);

        return new Section(keyHolder.getKey().longValue(), lineId, stationId, distance);
    }

    public boolean delete(Long lineId, Long stationId) {
        return jdbcTemplate.update(SectionDaoQuery.DELETE, lineId, stationId) > 0;
    }

    public Sections findByLineId(Long lineId) {
        return new Sections(jdbcTemplate.query(SectionDaoQuery.FIND_BY_LINE_ID, sectionRowMapper, lineId));
    }

    public int countByLineId(Long lineId) {
        return jdbcTemplate.queryForObject(SectionDaoQuery.COUNT_BY_LINE_ID, int.class, lineId);
    }

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("station_id"),
                    resultSet.getInt("distance"));
}
