package subway.station.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.exception.EntityNotFoundException;
import subway.station.dao.StationDao;
import subway.station.domain.Station;
import subway.station.dto.StationRequest;
import subway.station.dto.StationResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class StationService {
    private final StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest request) {
        validateName(request.getName());

        Station newStation = stationDao.insert(request.getName());
        return newStation.toDto();
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(Station::toDto)
                .collect(toList());
    }

    public StationResponse findById(Long id) {
        return stationDao.findById(id).toDto();
    }

    public void deleteById(Long id) {
        boolean isDeleted = stationDao.deleteById(id);
        if (!isDeleted) {
            throw new EntityNotFoundException("삭제하려는 역이 존재하지 않습니다.");
        }
    }

    private void validateName(String name) {
        if (stationDao.countByName(name) > 0) {
            throw new IllegalArgumentException("이미 등록된 역입니다.");
        }
    }
}
