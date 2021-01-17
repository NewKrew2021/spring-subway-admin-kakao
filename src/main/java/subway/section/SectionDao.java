package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        if (!canInsert(section)) {
            throw new RuntimeException();
        }

        if (existSameUpStationId(section.getLineId(), section.getUpStationId())) {
            Section oldSection = getSectionByUpStationId(section.getLineId(), section.getUpStationId());
            addNewBackwardSection(section, oldSection);
            deleteById(oldSection.getId());
        }

        if (existSameDownStationId(section.getLineId(), section.getDownStationId())) {
            Section oldSection = getSectionByDownStationId(section.getLineId(), section.getDownStationId());
            addNewForwardSection(section, oldSection);
            deleteById(oldSection.getId());
        }

        return insertAtDB(section);
    }

    public List<Section> findAllByLineId(Long id) {
        String selectByIdQuery = "select * from section where line_id = ?";
        return jdbcTemplate.query(selectByIdQuery, new SectionMapper(), id);
    }

    public List<Long> findSortedIdsByLineId(Long lineId) {
        List<Section> sections = findAllByLineId(lineId);

        Map<Long, Long> upStationToDownStation = new HashMap<>();
        for (Section section : sections) {
            upStationToDownStation.put(section.getUpStationId(), section.getDownStationId());
        }

        List<Long> stationIds = new ArrayList<>();

        Long currentId = getFirstStationId(lineId);
        while (currentId != null) {
            stationIds.add(currentId);
            currentId = upStationToDownStation.get(currentId);
        }

        return stationIds;
    }

    public void deleteById(Long id) {
        String deleteByIdQuery = "delete from section where id = ?";
        jdbcTemplate.update(deleteByIdQuery, id);
    }

    public void deleteStation(Long lineId, Long stationId) {
        Section forwardSection = getSectionByDownStationId(lineId, stationId);
        Section backwardSection = getSectionByUpStationId(lineId, stationId);

        if (forwardSection == null || backwardSection == null) {
            throw new RuntimeException();
        }

        deleteById(forwardSection.getId());
        deleteById(backwardSection.getId());

        Section newSection = new Section(
                forwardSection.getUpStationId(),
                backwardSection.getDownStationId(),
                lineId,
                forwardSection.getDistance() + backwardSection.getDistance()
        );

        insertAtDB(newSection);
    }

    private void addNewBackwardSection(Section section, Section oldSection) {
        Section newSection = new Section(
                section.getDownStationId(),
                oldSection.getDownStationId(),
                section.getLineId(),
                oldSection.getDistance() - section.getDistance()
        );

        insertAtDB(newSection);
    }

    private void addNewForwardSection(Section section, Section oldSection) {
        Section newSection = new Section(
                oldSection.getUpStationId(),
                section.getUpStationId(),
                section.getLineId(),
                oldSection.getDistance() - section.getDistance()
        );

        insertAtDB(newSection);
    }

    private boolean existSameUpStationId(Long lineId, Long upStationId) {
        return getSectionByUpStationId(lineId, upStationId) != null;
    }

    private boolean existSameDownStationId(Long lineId, Long downStationId) {
        return getSectionByDownStationId(lineId, downStationId) != null;
    }

    private boolean canInsert(Section section) {
        if (findAllByLineId(section.getLineId()).size() == 0) {
            return true;
        }

        boolean upStationExist = alreadyExistInLine(section.getLineId(), section.getUpStationId());
        boolean downStationExist = alreadyExistInLine(section.getLineId(), section.getDownStationId());

        if (upStationExist == downStationExist) {
            return false;
        }

        return true;
    }

    private Long getFirstStationId(Long id) {
        List<Section> sections = findAllByLineId(id);

        Map<Long, Boolean> isStartPoint = new HashMap<>();

        for (Section section : sections) {
            isStartPoint.put(section.getUpStationId(), true);
            isStartPoint.put(section.getDownStationId(), true);
        }

        for (Section section : sections) {
            isStartPoint.put(section.getDownStationId(), false);
        }

        return isStartPoint.keySet().stream()
                .filter(key -> isStartPoint.get(key))
                .findFirst()
                .orElseThrow(() -> new NotFoundException());
    }

    private Section getSectionByUpStationId(Long lineId, Long upStationId) {
        String selectByUpStationIdQuery = "select * from section where line_id = ? and up_station_id = ? limit 1";
        try {
            return jdbcTemplate.queryForObject(selectByUpStationIdQuery, new SectionMapper(), lineId, upStationId);
        } catch (Exception e) {
            return null;
        }
    }

    private Section getSectionByDownStationId(Long lineId, Long downStationId) {
        String selectByDownStationIdQuery = "select * from section where line_id = ? and down_station_id = ? limit 1";
        try {
            return jdbcTemplate.queryForObject(selectByDownStationIdQuery, new SectionMapper(), lineId, downStationId);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean alreadyExistInLine(Long lineId, Long stationId) {
        String countByStationIdQuery = "select count(*) from section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        int existCount = jdbcTemplate.queryForObject(countByStationIdQuery, int.class, lineId, stationId, stationId);
        return existCount != 0;
    }

    private Section insertAtDB(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement psmt = con.prepareStatement(
                    "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            psmt.setLong(1, section.getLineId());
            psmt.setLong(2, section.getUpStationId());
            psmt.setLong(3, section.getDownStationId());
            psmt.setInt(4, section.getDistance());
            return psmt;
        }, keyHolder);

        Long id = (Long) keyHolder.getKey();

        return new Section(
                id,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        );
    }

    private final static class SectionMapper implements RowMapper<Section> {
        @Override
        public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            int distance = rs.getInt("distance");

            return new Section(id, lineId, upStationId, downStationId, distance);
        }
    }
}