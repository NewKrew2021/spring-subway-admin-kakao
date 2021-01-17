package subway.station;

import org.springframework.util.ReflectionUtils;
import subway.exception.DuplicateNameException;
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
        stations.stream()
                .filter(value -> value.getName().equals(station.getName()))
                .findAny()
                .ifPresent(existed -> {
                    throw new DuplicateNameException("동일한 이름을 가진 지하철역이 이미 존재합니다.");
                });
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public Station findOne(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findAny()
                .orElseGet(() -> {
                    throw new NoContentException("해당 id를 갖는 지하철 역이 존재하지 않습니다.");
                });
    }

    public List<Station> findAll() {
        return stations;
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
