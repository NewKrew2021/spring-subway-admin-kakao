package subway.line;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SectionMapper implements RowMapper<Section>, Serializable {

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Section(rs.getLong("id"),
                rs.getLong("line_id"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                rs.getInt("distance"),
                rs.getBoolean("first_section"),
                rs.getBoolean("last_section"));
    }
}
