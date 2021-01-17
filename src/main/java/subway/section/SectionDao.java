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

    public Section save(Section section){
        if(!canInsert(section)){
           throw new RuntimeException();
        }

        if(existSameUpstationId(section.getLineId(), section.getUpStationId())) {
            Section oldSection = getSectionByUpStationId(section.getLineId(), section.getUpStationId());
            addNewBackwardSection(section, oldSection);
            deleteById(oldSection.getId());
        }

        if(existSameDownStationId(section.getLineId(), section.getDownStationId())) {
            Section oldSection = getSectionByDownStationId(section.getLineId(), section.getDownStationId());
            addNewForwardSection(section, oldSection);
            deleteById(oldSection.getId());
        }

        return insertAtDB(section);
    }

    private void addNewBackwardSection(Section section, Section oldSection){
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

    public boolean existSameUpstationId(Long lineId, Long upStationId) {
        if(getSectionByUpStationId(lineId, upStationId) != null) return true;
        return false;
    }

    public boolean existSameDownStationId(Long lineId, Long downStationId) {
        if(getSectionByDownStationId(lineId, downStationId) != null) return true;
        return false;
    }

    public void deleteById(Long id) {
        String query = "delete from section where id = ?";
        jdbcTemplate.update(query, id);
    }

    public List<Section> findAllByLineId(Long id){
        String query = "select * from section where line_id = ?";
        return jdbcTemplate.query(query, new SectionMapper(), id);
    }

    private Long getFirstStationId(Long id) {
        List<Section> sections = findAllByLineId(id);

        Map<Long, Boolean> isStartPoint = new HashMap<>();

        /* 모든 station에 대해 start point = true 라는 의미로 초기화 */
        for(Section section : sections){
            isStartPoint.put(section.getUpStationId(), true);
            isStartPoint.put(section.getDownStationId(), true);
        }

        /* 모든 section의 downStation Id에 대해 false 처리함 */
        for(Section section : sections){
            isStartPoint.put(section.getDownStationId(), false);
        }

        /* downStation으로 등장하지 않은 한개의 노드(시작점) 을 return */
        return isStartPoint.keySet().stream()
                .filter(key -> isStartPoint.get(key))
                .findFirst()
                .orElseThrow(() -> new NotFoundException());
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, upstationId가 일치하는 section을 return
     */
    private Section getSectionByUpStationId(Long lineId, Long upStationId){
        try {
            String sqlQuery = "select * from section where line_id = ? and up_station_id = ? limit 1";
            return jdbcTemplate.queryForObject(sqlQuery, new SectionMapper(), lineId, upStationId);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, downStationId가 일치하는 section을 return
     */
    private Section getSectionByDownStationId(Long lineId, Long downStationId){
        try{
            String sqlQuery = "select * from section where line_id = ? and down_station_id = ? limit 1";
            return jdbcTemplate.queryForObject(sqlQuery, new SectionMapper(), lineId, downStationId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Long> findSortedIdsByLineId(Long lineId){
        /* 주어진 Line 위에 정의된 모든 section들을 collect */
        List<Section> sections = findAllByLineId(lineId);

        /* 현재 station에서 다음 station을 참조할 수 있는 map을 생성 */
        Map<Long, Long> upStationToDownStation = new HashMap<>();
        for(Section section : sections){
            upStationToDownStation.put(section.getUpStationId(), section.getDownStationId());
        }

        /* 정렬된 station id가 저장될 곳 */
        List<Long> stationIds = new ArrayList<>();

        /* 일렬로 탐색하면서 station id를 수집한다. */
        Long currentId = getFirstStationId(lineId);
        while(currentId != null) {
            stationIds.add(currentId);
            currentId = upStationToDownStation.get(currentId);
        }

        return stationIds;
    }

    public boolean canInsert(Section section){
        if(findAllByLineId(section.getLineId()).size() == 0) {
            return true;
        }

        boolean upStationExist = alreadyExistInLine(section.getLineId(), section.getUpStationId());
        boolean downStationExist = alreadyExistInLine(section.getLineId(), section.getDownStationId());

        /* 둘다 등록되었거나, 둘다 등록되어 있지 않는 경우 */
        if(upStationExist == downStationExist){
            return false;
        }

        return  true;
    }

    private boolean alreadyExistInLine(Long lineId, Long stationId){
        String sqlQuery = "select count(*) from section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        int existCount = jdbcTemplate.queryForObject(sqlQuery, int.class, lineId, stationId, stationId);
        return existCount != 0;
    }

    public void deleteStation(Long lineId, Long stationId) {
        Section forwardSection = getSectionByDownStationId(lineId, stationId);
        Section backwardSection = getSectionByUpStationId(lineId, stationId);

        if(forwardSection == null || backwardSection == null) {
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

    private final static class SectionMapper implements RowMapper<Section> {
        @Override
        public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId= rs.getLong("down_station_id");
            int distance = rs.getInt("distance");

            return new Section(id, lineId, upStationId, downStationId, distance);
        }
    }
}