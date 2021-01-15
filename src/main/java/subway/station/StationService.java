package subway.station;

import java.util.List;

public interface StationService {
    public Station save(Station station);

    public List<Station> findAll();

    public Station findOne(Long stationId);

    public boolean deleteById(Long stationId);
}
