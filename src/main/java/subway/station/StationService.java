package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest request) {
        if (stationDao.isExistingName(request.getName())) {
            throw new IllegalArgumentException("이미 등록된 지하철역 입니다.");
        }
        Station newStation = stationDao.insert(new Station(request.getName()));
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

    public boolean deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
