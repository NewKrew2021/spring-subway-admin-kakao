package subway.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Station;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StationMapper implements RowMapper<Station>, Serializable {

    @Override
    public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Station(rs.getLong("id"),
                rs.getString("name"));
    }
}
