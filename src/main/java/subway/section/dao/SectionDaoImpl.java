package subway.section.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import subway.section.entity.Section;
import subway.section.entity.Sections;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class SectionDaoImpl implements SectionDao {
    private static final String INSERT_QUERY = "insert into section(line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
    private static final String SELECT_BY_ID_QUERY = "select * from section where id = ?";
    private static final String SELECT_BY_LINE_ID_QUERY = "select * from section where line_id = ?";
    private static final String UPDATE_QUERY = "update section set line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
    private static final String DELETE_QUERY = "delete from section where id = ?";
    private static final String DELETE_BY_IDS_QUERY = "delete from section where id in (%s)";
    private static final String DELETE_BY_LINE_ID_QUERY = "delete from section where line_id = ?";
    private static final RowMapper<Section> sectionRowMapper = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance")
    );

    private final JdbcTemplate jdbcTemplate;

    public SectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section insert(Long lineId, Long upStationId, Long downStationId, int distance) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = generatePreparedStatementCreator(lineId, upStationId, downStationId, distance);
        jdbcTemplate.update(psc, keyHolder);
        Long id = (Long) keyHolder.getKey();
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    private PreparedStatementCreator generatePreparedStatementCreator(Long lineId, Long upStationId, Long downStationId, int distance) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, lineId);
            ps.setLong(2, upStationId);
            ps.setLong(3, downStationId);
            ps.setInt(4, distance);
            return ps;
        };
    }

    @Override
    public Section insert(Section section) {
        return insert(section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }

    @Override
    public Optional<Section> findSectionById(Long id) {
        try {
            Section section = jdbcTemplate.queryForObject(SELECT_BY_ID_QUERY, sectionRowMapper, id);
            return Optional.ofNullable(section);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Sections> findSectionsByLineId(Long lineId) {
        try {
            List<Section> sectionList = jdbcTemplate.query(SELECT_BY_LINE_ID_QUERY, sectionRowMapper, lineId);
            Sections sections = new Sections(sectionList);
            return Optional.of(sections);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int update(Section section) {
        return jdbcTemplate.update(UPDATE_QUERY,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                section.getId()
        );
    }

    @Override
    public int delete(Long id) {
        return jdbcTemplate.update(DELETE_QUERY, id);
    }

    @Override
    public int delete(List<Long> ids) {
        String joinedIds = getJoinedString(ids);
        String sql = String.format(DELETE_BY_IDS_QUERY, joinedIds);
        return jdbcTemplate.update(sql);
    }

    private String getJoinedString(List<Long> ids) {
        return ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    @Override
    public int deleteByLineId(Long lineId) {
        return jdbcTemplate.update(DELETE_BY_LINE_ID_QUERY, lineId);
    }
}
