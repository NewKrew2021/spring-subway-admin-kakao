package subway.station;

import org.springframework.util.ReflectionUtils;
import subway.exceptions.DuplicateStationNameException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        if (isExistStations(station)) {
            throw new DuplicateStationNameException("중복된 역 이름입니다.");
        }
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static boolean isExistStations(Station station) {
        return stations.contains(station);
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static boolean deleteById(Long id) {
        return stations.removeIf(it -> it.getId().equals(id));
    }

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
