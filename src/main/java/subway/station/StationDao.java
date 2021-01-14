package subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static StationDao stationDao = null;
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    private StationDao() {}

    public static StationDao getInstance() {
        if (stationDao == null){
            stationDao = new StationDao();
        }
        return stationDao;
    }

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        if(stations.contains(station)){
            throw new IllegalArgumentException("역이 중복됩니다.");
        }
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    public Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst();
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
