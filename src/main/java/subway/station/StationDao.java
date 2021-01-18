package subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();
    private static StationDao stationDao;

    private StationDao() {}

    public static StationDao getInstance() {
        if (stationDao == null) {
            stationDao = new StationDao();
        }
        return stationDao;
    }

    public Station save(Station station) {
        if(isExist(station)){
            return stations.stream()
                    .filter(station1 -> station1.getName().equals(station.getName()))
                    .findFirst()
                    .orElse(null);
        }
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private boolean isExist(Station station) {
        return stations.stream()
                .map(Station::getName)
                .collect(Collectors.toList())
                .contains(station.getName());
    }

    public List<Station> findAll() {
        return stations;
    }

    public Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElse(null);
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
