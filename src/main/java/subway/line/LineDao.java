package subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.section.Section;
import subway.section.SectionDao;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class LineDao {
    private List<Line> lines = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    public LineDao(SectionDao sectionDao, JdbcTemplate jdbcTemplate) {
        this.sectionDao = sectionDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line, Section section) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("line")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        sectionDao.save(new Section(section.getUpStationId(), section.getDownStationId(), section.getDistance(), id));
        return new Line(id, line.getColor(), line.getName());
    }

    public int deleteById(Long lineId) {
        String sql = "delete from LINE where id = ?";
        return jdbcTemplate.update(sql, lineId);
    }

    public List<Line> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("color"), rs.getString("name")));
    }

    public Line findOne(Long lineId) {
        String sql = "select * from LINE where id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("color"), rs.getString("name")), lineId);
    }

    public void update(Line line) {
        lines.remove(findOne(line.getId()));
        lines.add(line);
    }

    public boolean saveSection(Long lineId, Section section) {
        Line line = lines.stream().filter(l -> l.getId().equals(lineId)).findFirst().get();
        List<Section> sections = line.getSections();
        int sectionIdx = -1;
        boolean upFlag = false, downFlag = false;
        Long upId = section.getUpStationId(), downId = section.getDownStationId(), stationId = -1L;
        for (int i = 0; i < sections.size(); i++) {
            if (upId == sections.get(i).getUpStationId() || upId == sections.get(i).getDownStationId()) {
                sectionIdx = i;
                stationId = upId;
                upFlag = true;
            }

            if (downId == sections.get(i).getUpStationId() || downId == sections.get(i).getDownStationId()) {
                sectionIdx = i;
                stationId = downId;
                downFlag = true;
            }
        }
        if (upFlag && downFlag) {
            return false;
        }
        if (sectionIdx == -1) {
            return false;
        }
        if (stationId == upId) {
            sections.add(sectionIdx, section);
            if (sectionIdx < sections.size() - 1) {
                Long nextStationId = sections.get(sectionIdx + 1).getDownStationId();
                Integer nextStationDistance = sections.get(sectionIdx + 1).getDistance() - section.getDistance();
                sections.remove(sectionIdx + 1);
                sections.add(sectionIdx + 1, new Section(downId, nextStationId, nextStationDistance, lineId));
            }
        }

        if (stationId == downId) {
            sections.add(sectionIdx, section);
            if (sectionIdx > 0) {
                Long prevStationId = sections.get(sectionIdx - 1).getDownStationId();
                Integer prevStationDistance = sections.get(sectionIdx - 1).getDistance() - section.getDistance();
                sections.remove(sectionIdx - 1);
                sections.add(sectionIdx - 1, new Section(prevStationId, upId, prevStationDistance, lineId));
            }
        }
        return true;
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Line line = findOne(lineId);
        List<Section> sections = line.getSections();
        if (sections.size() == 1) {
            return false;
        }
        int upIdx = -1, downIdx = -1;

        boolean upFlag = false, downFlag = false;
        for (int i = 0; i < sections.size(); i++) {
            if (stationId == sections.get(i).getUpStationId()) {
                upIdx = i;
                upFlag = true;
            }

            if (stationId == sections.get(i).getDownStationId()) {
                downIdx = i;
                downFlag = true;
            }
        }
        if (!upFlag && !downFlag) {
            return false;
        } else if (upFlag && downFlag) {
            Long nextStationId = sections.get(upIdx).getDownStationId();
            Integer nextDistance = sections.get(upIdx).getDistance();
            Long prevStationId = sections.get(downIdx).getUpStationId();
            Integer prevDistance = sections.get(downIdx).getDistance();

            sections.remove(upIdx);
            sections.remove(downIdx);
            sections.add(downIdx, new Section(prevStationId, nextStationId, nextDistance + prevDistance, lineId));
        } else if (upFlag) {
            sections.remove(upIdx);
        } else if (downFlag) {
            sections.remove(downIdx);
        }

        return true;
    }
}
