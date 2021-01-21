package subway.station.domain;

import subway.station.dto.StationResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Stations {
    private List<Station> stations;

    public Stations(List<Station> stations){
        this.stations = stations;
    }

    public List<StationResponse> getStationResponse(){
        return stations.stream()
                .distinct()
                .filter(distinctByKey(station -> station.getName()))
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
