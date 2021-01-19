package subway.station;

import java.util.List;
import java.util.stream.Collectors;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<StationResponse> getStationResponses(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(),
                        station.getName()))
                .collect(Collectors.toList());
    }
}
