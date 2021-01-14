package subway.station;

import org.springframework.util.ReflectionUtils;
import subway.line.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteById(Long id) {
        stations.removeIf(it -> it.getId().equals(id));
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static Station findById(Long stationId) {
        return stations.stream()
                .filter(station -> station.getId() == stationId)
                .findFirst()
                .orElse(null);
    }
}
