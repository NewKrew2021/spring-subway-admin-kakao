package subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exceptions.NotFoundException;
import subway.exceptions.CannotConstructRightSectionsForLine;
import subway.station.Station;

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
        if(!canInsert(section)) {
            throw new CannotConstructRightSectionsForLine("잘못된 section 저장입니다.");
        }

        /* 중간에 끼워 넣는경우 1 : upstation을 기준으로 기존 section을 삭제하고, 새롭게 연결해주어야 할 section을 추가 */
        if(existSameUpstationId(section.getLineId(), section.getUpStationId())) {
            Section oldSection = getSectionByUpStationId(section.getLineId(), section.getUpStationId());
            addNewBackwardSection(section, oldSection);
            deleteById(oldSection.getId());
        }

        /* 중간에 끼워 넣는 경우 2 : downstation을 기준으로 기존 section을 삭제하고, 새롭게 연결해주어야 할 section을 추가 */
        if(existSameDownStationId(section.getLineId(), section.getDownStationId())) {
            Section oldSection = getSectionByDownStationId(section.getLineId(), section.getDownStationId());
            addNewForwardSection(section, oldSection);
            deleteById(oldSection.getId());
        }

        /* 전달된 새로운 section을 추가 */
        return insertAtDB(section);
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



    /**
     * 한 라인에 존재하는 모든 section들 중에서, upstationId가 일치하는 section을 return
     */
    private Section getSectionByUpStationId(Long lineId, Long upStationId) {
        try {
            String sqlQuery = "select * from section where line_id = ? and up_station_id = ? limit 1";
            return jdbcTemplate.queryForObject(sqlQuery, new SectionMapper(), lineId, upStationId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, downStationId가 일치하는 section을 return
     */
    private Section getSectionByDownStationId(Long lineId, Long downStationId) {
        try {
            String sqlQuery = "select * from section where line_id = ? and down_station_id = ? limit 1";
            return jdbcTemplate.queryForObject(sqlQuery, new SectionMapper(), lineId, downStationId);
        } catch (Exception e) {
            return null;
        }
    }



    public boolean canInsert(Section section) {

        /* line을 처음 생성하는 경우는 통과 */
        if(findAllByLineId(section.getLineId()).size() == 0) {
            return true;
        }

        boolean upStationExist = alreadyExistInLine(section.getLineId(), section.getUpStationId());
        boolean downStationExist = alreadyExistInLine(section.getLineId(), section.getDownStationId());

        /* 둘다 등록되었거나, 둘다 등록되어 있지 않는 경우 */
        if(upStationExist == downStationExist) {
            return false;
        }

        return  true;
    }

    private boolean alreadyExistInLine(Long lineId, Long stationId) {
        String sqlQuery = "select count(*) from section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        int existCount = jdbcTemplate.queryForObject(sqlQuery, int.class, lineId, stationId, stationId);
        return existCount != 0;
    }

    public void deleteStation(Long lineId, Long stationId) {
        deleteableCheck(lineId, stationId);

        Section forwardSection = getSectionByDownStationId(lineId, stationId);
        Section backwardSection = getSectionByUpStationId(lineId, stationId);

        /* 삭제할 station이 상행 종점인 경우 */
        if(null == forwardSection) {
            deleteById(backwardSection.getId());
            return;
        }

        /* 삭제한 station이 하행 종점인 경우 */
        if(null == backwardSection) {
            deleteById(forwardSection.getId());
            return;
        }

        /* 종점이 아닌 station을 삭제하는 경우 */
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

    private void deleteableCheck(Long lineId, Long stationId) {
        if(!alreadyExistInLine(lineId, stationId)) {
            throw new NotFoundException("삭제할 station이 존재하지 않습니다.");
        }

        /* 존재하는 section의 수가 1 이하일 경우 */
        if(findAllByLineId(lineId).size() <= 1) {
            throw new CannotConstructRightSectionsForLine(findAllByLineId(lineId).size() + " " + "해당 line에서 더 이상 station을 삭제할 수 없습니다.");
        }
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