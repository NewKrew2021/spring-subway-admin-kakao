package subway.station;

import org.springframework.util.ReflectionUtils;
import subway.exception.NotExistException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    public void deleteById(Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }

    public Station findById(Long id) {
//        try {
//            return stations.stream().filter(station -> station.getId().equals(id)).findFirst().get();
//        } catch (NoSuchElementException e) {
//            throw new NotExistExcepton("해당 역이 존재하지 않습니다.");
//        }
        try {
            return stations.stream().filter(station -> station.getId().equals(id)).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
