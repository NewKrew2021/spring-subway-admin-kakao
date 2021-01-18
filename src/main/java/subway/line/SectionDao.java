package subway.line;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

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

    public Section findByStationId(Long lineId, Long stationId) {
        Section upSection = findByUpStationId(lineId, stationId);
        Section downSection = findByDownStationId(lineId, stationId);

        return upSection == null ? downSection : upSection;
    }

    public boolean insert(Long lineId, SectionRequest request) {
        Long upStationId = request.getUpStationId();
        Long downStationId = request.getDownStationId();
        int distance = request.getDistance();

        Section upSection = findByStationId(lineId, upStationId);
        Section downSection = findByStationId(lineId, downStationId);

        // 둘 다 없거나, 둘다 있는 케이스
        if ((upSection == null && downSection == null) || (upSection != null && downSection != null)) {
            return false;
        }

        // 종점이 포함되어 있으면, 업데이트 없이 생성만 해주면 됨. 거리유효성 검사도 노필요.
        // 종점이 아니면, 생성 + 업데이트
        // 상행 종점
        if (upSection == null && findByDownStationId(lineId, downStationId) == null) {
            String insertSql = "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
            jdbcTemplate.update(insertSql, lineId, upStationId, downStationId, distance);

            return true;
        }
        // 하행 종점
        if (downSection == null && findByUpStationId(lineId, upStationId) == null) {
            String insertSql = "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
            jdbcTemplate.update(insertSql, lineId, upStationId, downStationId, distance);

            return true;
        }
        // not 종점
//        upStationId, middleStationId, downStationId

        String insertSql = "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
        Long middleStationId;
        if (upSection != null) {
            deleteById(upSection.getId());

            upStationId = upSection.getUpStationId();
            middleStationId = downStationId;
            downStationId = upSection.getDownStationId();
            int dis = upSection.getDistance() - distance;

            if (dis <= 0) {
                return false;
            }

            jdbcTemplate.update(insertSql, lineId, upStationId, middleStationId, distance);
            jdbcTemplate.update(insertSql, lineId, middleStationId, downStationId, dis);
        } else {
            deleteById(downSection.getId());

            upStationId = downSection.getUpStationId();
            middleStationId = upStationId;
            downStationId = downSection.getDownStationId();
            int dis = downSection.getDistance() - distance;

            if (dis <= 0) {
                return false;
            }

            jdbcTemplate.update(insertSql, lineId, upStationId, middleStationId, dis);
            jdbcTemplate.update(insertSql, lineId, middleStationId, downStationId, distance);
        }

        return true;
    }

    public boolean delete(Long lineId, Long stationId) {
        // Line 존재 여부 및 삭제 가능 여부 판단
        if (findByLineId(lineId).size() <= 1) {
            return false;
        }
        // Station 존재 여부 판단
        Section upSection = findByDownStationId(lineId, stationId);
        Section downSection = findByUpStationId(lineId, stationId);

        // 둘다 null or 둘다
        if (upSection == null && downSection == null) {
            return false;
        }

        if (upSection != null && downSection != null) {
            int distance = upSection.getDistance() + downSection.getDistance();

            insertDirectly(lineId, upSection.getUpStationId(), downSection.getDownStationId(), distance);
        }

        deleteById(upSection.getId());
        deleteById(downSection.getId());
        System.out.println("1111"+findByLineId(1L).get(0).getDownStationId());
        System.out.println("1111"+findByLineId(1L).get(0).getUpStationId());
        System.out.println("1111"+findByLineId(1L).get(1).getDownStationId());
        System.out.println("1111"+findByLineId(1L).get(1).getUpStationId());

        return true;
    }

    public boolean insertDirectly(Long lineId, Long upStationId, Long downStationId, int distance) {
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
        return jdbcTemplate.update(sql, lineId, upStationId, downStationId, distance) > 0;
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public Section findByUpStationId(Long lineId, Long upStationId) {
        String sql = "select * from section where up_station_id = ? and line_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, sectionRowMapper, upStationId, lineId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public Section findByDownStationId(Long lineId, Long downStationId) {
        String sql = "select * from section where down_station_id = ? and line_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, sectionRowMapper, downStationId, lineId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Section> findAll() {
        String sql = "select * from section";
        return jdbcTemplate.query(sql, sectionRowMapper);
    }

    public boolean deleteById(Long id) {
        String sql = "delete from section where id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public boolean isValid(Section section) {
        return section.getDownStationId() != section.getUpStationId();
    }

    private boolean isDuplicateSection(Long upStationId, Long downStationId) {
        String sql = "select count(1) from section where up_station_id = ? and down_station_id = ?";
        return jdbcTemplate.queryForObject(sql, int.class, upStationId, downStationId) > 0;
    }

    public Section findFirstStation(Long lineId) {
        List<Section> sections = findByLineId(lineId);
        Map<Long, Long> m = new HashMap<>();
        for (Section section : sections) {
            m.put(section.getDownStationId(), section.getUpStationId());
        }

        return findByUpStationId(lineId, traceFirst(m, sections.get(0).getUpStationId()));
    }

    private long traceFirst(Map<Long, Long> m, Long downStationId) {
        if (!m.containsKey(downStationId)) {
            return downStationId;
        }
        return traceFirst(m, m.get(downStationId));
    }

    public Section findFirstStation2(Long lineId) {
        List<Section> sections = findByLineId(lineId);

        Map<Long, Integer> m = new HashMap<>();

        for (Section section : sections) {
            m.put(section.getUpStationId(), 0);
        }

        for (Section section : sections) {
            m.put(section.getDownStationId(), 1);
        }

        for (Section section : sections) {
            if (m.get(section.getUpStationId()) == 0)
                return section;
        }
        return sections.get(0);
    }
}
