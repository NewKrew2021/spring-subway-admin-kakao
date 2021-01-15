package subway.station;

import java.util.List;

public interface StationService {
    public StationResponse save(StationRequest stationRequest);

    public List<StationResponse> findAll();

    public StationResponse findOne(Long stationId);

    public boolean deleteById(Long stationId);
}
