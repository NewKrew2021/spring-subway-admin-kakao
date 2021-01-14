package subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static final StationDao stationDao = new StationDao();
    private static final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    private StationDao() {}

    public static StationDao getInstance() {
        return stationDao;
    }

    public static void clear() {
        stations.clear();
    }

    public Station save(Station station) {
        if (isExist(station.getName())) {
            throw new IllegalStateException("이미 등록된 지하철역 입니다.");
        }

        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private boolean isExist(String name) {
        return stations.stream()
                .anyMatch(station -> station.getName().equals(name));
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

    public Optional<Station> findById(long id) {
        return stations.stream()
                .filter(station -> station.getId()==id)
                .findAny();
    }
}
