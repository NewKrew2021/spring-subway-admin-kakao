package subway.station;

import org.springframework.util.ReflectionUtils;
import subway.exception.NoContentException;
import subway.line.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {

    private static final StationDao instance = new StationDao();
    private static final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    private StationDao() {
    }

    public static StationDao getInstance() {
        return instance;
    }

    public Station save(Station station) {
        return stations.stream()
                .filter(value -> value.getName().equals(station.getName()))
                .findAny()
                .orElseGet(() -> {
                    Station persistStation = createNewObject(station);
                    stations.add(persistStation);
                    return persistStation;
                });
    }

    public List<Station> findAll() {
        return stations;
    }

    public Station findOne(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findAny()
                .orElseGet(() -> {
                    throw new NoContentException(id + "(Station)");
                });
    }

    public void deleteById(Long id) {
        stations.removeIf(it -> it.getId().equals(id));
    }

    public void deleteAll() {
        stations.clear();
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
