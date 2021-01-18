package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.line.Line;
import subway.line.LineDao;
import subway.station.StationDao;

import java.sql.PreparedStatement;
import java.sql.Statement;

import static subway.section.SectionQuery.*;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    insertQuery,
                    Statement.RETURN_GENERATED_KEYS);
            psmt.setLong(1, section.getLine().getId());
            psmt.setLong(2, section.getUpStation().getId());
            psmt.setLong(3, section.getDownStation().getId());
            psmt.setInt(4, section.getDistance());
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();

        return new Section(
                id,
                section.getLine(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }

    public SectionsInLine findAllByLine(Line line, StationDao stationDao, LineDao lineDao) {
        return new SectionsInLine(jdbcTemplate.query(selectByIdQuery, new SectionMapper(stationDao, lineDao), line.getId()));
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(deleteByIdQuery, id);
    }

}