package subway.station;

import org.springframework.util.ReflectionUtils;

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
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    public void deleteById(Long id) {
        stations.removeIf(it -> it.getId().equals(id));
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
