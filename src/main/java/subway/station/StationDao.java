package subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationDao {
    private final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    public Station save(Station station) throws SQLException {
        Station persistStation = createNewObject(station);

        if (isExists(station)) {
            throw new SQLException();
        }

        stations.add(persistStation);
        return persistStation;
    }

    private boolean isExists(Station station) {
        return stations.stream()
                .anyMatch(stationIn -> station.getName().equals(stationIn.getName()));
    }

    public Station getById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Station> findAll() {
        return Collections.unmodifiableList(stations);
    }

    public boolean deleteById(Long id) {
        return stations.removeIf(it -> it.getId().equals(id));
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
