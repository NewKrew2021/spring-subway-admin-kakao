package subway.service;

import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

import java.util.List;

public interface StationService {
    Station save(Station station);

    List<Station> findAll();

    Station findOne(Long stationId);

    boolean deleteById(Long stationId);

}
