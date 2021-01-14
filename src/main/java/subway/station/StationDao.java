package subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();
    private static StationDao instance;

    private StationDao() {
    }

    public static StationDao getInstance(){
        if(instance == null){
            instance = new StationDao();
        }
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

    public Station findOne(Long stationId){
        return stations.stream()
                .filter(s -> s.getId().equals(stationId))
                .findFirst()
                .get();
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
