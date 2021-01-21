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

    public Sections insertOnCreateLine(Long lineId, Long upStationId, Long downStationId, int distance) {
        List<Section> sections = new ArrayList<>();

        sections.add(insertSection(lineId, upStationId, 0));
        sections.add(insertSection(lineId, downStationId, distance));

        return new Sections(sections);
    }

    public void insert(Long lineId, Long upStationId, Long downStationId, int distance) {
        Sections sections = findByLineId(lineId);
        Section newSection = sections.insert(upStationId, downStationId, distance);
        insertSection(newSection.getLineId(), newSection.getStationId(), newSection.getDistance());
    }

    public Section insertSection(Long lineId, Long stationId, int distance) {
        String sql = "insert into section (line_id, station_id, distance) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement st = con.prepareStatement(sql, new String[]{"id"});
            st.setLong(1, lineId);
            st.setLong(2, stationId);
            st.setInt(3, distance);
            return st;
        }, keyHolder);

        return new Section(keyHolder.getKey().longValue(), lineId, stationId, distance);
    }

    public boolean delete(Long lineId, Long stationId) {
        String sql = "delete from section where line_id = ? and station_id = ?";
        return jdbcTemplate.update(sql, lineId, stationId) > 0;
    }

    public Sections findByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, sectionRowMapper, lineId));
    }

    private final RowMapper<Section> sectionRowMapper =
            (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("station_id"),
                    resultSet.getInt("distance"));

    public int countByLineId(Long lineId) {
        String sql = "select count(*) from section where line_id = ?";
        return jdbcTemplate.queryForObject(sql, int.class, lineId);
    }
}
