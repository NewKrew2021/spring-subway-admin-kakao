package subway.station;

import java.util.List;

public interface StationService {
    Station save(Station station);

    List<Station> findAll();

    Station findOne(Long stationId);

    boolean deleteById(Long stationId);
}
