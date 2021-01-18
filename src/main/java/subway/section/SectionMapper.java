package subway.section;

import org.springframework.jdbc.core.RowMapper;
import subway.exception.NotFoundException;
import subway.line.Line;
import subway.line.LineDao;
import subway.station.Station;
import subway.station.StationDao;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SectionMapper implements RowMapper<Section> {
    StationDao stationDao;
    LineDao lineDao;

    public SectionMapper(StationDao stationDao, LineDao lineDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        Line line = lineDao.findById(rs.getLong("line_id")).orElseThrow(NotFoundException::new);
        Station upStation = stationDao.findById(rs.getLong("up_station_id")).orElseThrow(NotFoundException::new);
        Station downStation = stationDao.findById(rs.getLong("down_station_id")).orElseThrow(NotFoundException::new);
        int distance = rs.getInt("distance");

        return new Section(id, line, upStation, downStation, distance);
    }
}
