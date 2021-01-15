package subway.station;

import org.springframework.util.ReflectionUtils;
import subway.line.Section;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();
    private static StationDao stationDao;

    public static StationDao getStationDao(){
        if(stationDao==null){
            stationDao=new StationDao();
        }
        return stationDao;
    }

    public void init(){
        stations = new ArrayList<>();
        seq = 0L;
    }

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public Station findById(Long id){
        return stations
                .stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean hasSameStationName(Station station){
        return stations
                .stream()
                .anyMatch(i -> i.getName().equals(station.getName()));
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

    public List<StationResponse> getStationResponseList(List<Station> station){
        return station
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }
}
