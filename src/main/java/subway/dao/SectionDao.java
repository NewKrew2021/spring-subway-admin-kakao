package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SectionDao {
    private final StationDao stationDao;
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(StationDao stationDao, JdbcTemplate jdbcTemplate) {
        this.stationDao = stationDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, section.getLineId());
            pstmt.setLong(2, section.getUpStation().getId());
            pstmt.setLong(3, section.getDownStation().getId());
            pstmt.setLong(4, section.getDistance());
            return pstmt;
        }, keyHolder);
        return new Section(
                keyHolder.getKey().longValue(),
                section.getLineId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance());
    }

    public Section findOne(Long id) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from section where id = ?";
        return jdbcTemplate.queryForObject(
                sql,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("id"),
                        resultSet.getLong("line_id"),
                        stationDao.findOne(resultSet.getLong("up_station_id")),
                        stationDao.findOne(resultSet.getLong("down_station_id")),
                        resultSet.getInt("distance")
                ), id);
    }

    public List<Section> findAll(Long lineId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = ?";
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("id"),
                        resultSet.getLong("line_id"),
                        stationDao.findOne(resultSet.getLong("up_station_id")),
                        stationDao.findOne(resultSet.getLong("down_station_id")),
                        resultSet.getInt("distance")
                ), lineId);

    }

    public Section update(Section section) {
        String sql = "update section set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, section.getUpStation().getId());
            pstmt.setLong(2, section.getDownStation().getId());
            pstmt.setInt(3, section.getDistance());
            pstmt.setLong(4, section.getId());
            return pstmt;
        }, keyHolder);
        return new Section(keyHolder.getKey().longValue(),
                section.getLineId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance());
    }

    public void deleteById(Long id) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        String sql = "delete from section";
        jdbcTemplate.update(sql);
    }

    public Section findOneByLineIdAndStationId(Long lineId, Long stationId, boolean isUpStation) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from section where up_station_id = ? and line_id = ?";
        if (!isUpStation) {
            sql = "select id, line_id, up_station_id, down_station_id, distance from section where down_station_id = ? and line_id = ?";
        }
        List<Section> ret = jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("id"),
                        resultSet.getLong("line_id"),
                        stationDao.findOne(resultSet.getLong("up_station_id")),
                        stationDao.findOne(resultSet.getLong("down_station_id")),
                        resultSet.getInt("distance")
                ), stationId, lineId);
        if(ret.size()==0){
            return null;
        }
        return ret.get(0);
    }
}
