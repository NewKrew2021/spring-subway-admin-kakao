package subway.line.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.line.domain.Line;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LineMapper implements RowMapper<Line>, Serializable {

    @Override
    public Line mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Line(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                rs.getInt("distance"));
    }
}
