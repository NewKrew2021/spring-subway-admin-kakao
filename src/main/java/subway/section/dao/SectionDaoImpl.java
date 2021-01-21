package subway.section.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class SectionDaoImpl implements SectionDao {
    private static final String INSERT_QUERY = "insert into section(line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
    private static final String SELECT_ALL_QUERY = "select * from section";
    private static final String SELECT_BY_ID_QUERY = "select * from section where id = ?";
    private static final String SELECT_BY_LINE_ID_QUERY = "select * from section where line_id = ?";
    private static final String UPDATE_QUERY = "update section set line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
    private static final String DELETE_QUERY = "delete from section where id = ?";
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
    public Section insert(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    INSERT_QUERY,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        Long id = (Long) keyHolder.getKey();
        return new Section(id, section);
    }

    @Override
    public Optional<Section> findSectionById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(SELECT_BY_ID_QUERY, sectionRowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Sections findSectionsByLineId(Long lineId) {
        return new Sections(
                jdbcTemplate.query(SELECT_BY_LINE_ID_QUERY, sectionRowMapper, lineId)
        );
    }

    @Override
    public Sections findAllSections() {
        return new Sections(
                jdbcTemplate.query(SELECT_ALL_QUERY, sectionRowMapper)
        );
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
}
