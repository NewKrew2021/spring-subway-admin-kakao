package subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();
    private static StationDao stationDao = new StationDao();

    public static StationDao getInstance() {
        return stationDao;
    }

    public List<StationResponse> getStationResponses(){
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public static void clear() {
        getInstance().stations.clear();
        getInstance().seq = 0L;
    }

    public Station save(Station station) {
        Station foundStation = stations.stream()
                .filter(tmpstation -> tmpstation.getName().equals(station.getName()))
                .findAny()
                .orElse(null);
        if(foundStation != null)
            return foundStation;
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

    @Override
    public String toString() {
        return "StationDao{" +
                "seq=" + seq +
                ", stations=" + stations.stream().map(Station::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
