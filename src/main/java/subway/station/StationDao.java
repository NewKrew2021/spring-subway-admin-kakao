package subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import subway.section.*;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    private static StationDao instance;
    public static StationDao getInstance(){
        if(instance == null)
            instance = new StationDao();
        return instance;
    }
    private StationDao(){}

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    public Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId()==id)
                .collect(Collectors.toList()).get(0);
    }

    public List<Station> findByName(String name) {
        return stations.stream()
                .filter(station -> station.getName().equals(name))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if(subway.section.SectionDao.getInstance().contain(id)){
            throw new subway.exceptions.InvalidValueException();
        }
        stations.removeIf(it -> it.getId().equals(id));
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
