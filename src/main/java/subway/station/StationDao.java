package subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StationDao {
    private static StationDao stationDao = new StationDao();

    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public static void clear() {
        getInstance().stations.clear();
        getInstance().seq = 0L;
    }

    public Station save(Station station) {
        Station foundStation = stations.stream()
                .filter(tmpStation -> tmpStation.getName().equals(station.getName()))
                .findAny()
                .orElse(null);
        if (foundStation != null)
            return foundStation;
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void deleteById(Long id) {
        stations.removeIf(it -> it.getId().equals(id));
    }

    public List<Station> findAll() {
        return stations;
    }

    public List<StationResponse> getStationResponses() {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public static StationDao getInstance() {
        return stationDao;
    }

    @Override
    public String toString() {
        return "StationDao{" +
                "seq=" + seq +
                ", stations=" + stations.stream().map(Station::toString).collect(Collectors.joining(", ")) +
                '}';
    }

    //TODO
    public Station getStationById(Long stationId) {
        return stations.stream()
                .filter(tmpStation -> tmpStation.getId().equals(stationId))
                .findAny()
                .orElse(null);
    }

}
