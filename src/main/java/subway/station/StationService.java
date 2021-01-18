package subway.station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest request) {
        if (stationDao.existsBy(request.getName())) {
            throw new IllegalArgumentException("이미 등록된 지하철역 입니다.");
        }
        return StationResponse.from(stationDao.save(request.toEntity()));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::from)
                .collect(toList());
    }

    public void delete(Long id) {
        stationDao.deleteById(id);
    }
}
