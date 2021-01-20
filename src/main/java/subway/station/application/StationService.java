package subway.station.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.station.domain.StationCreateValue;
import subway.station.domain.StationDao;
import subway.station.presentation.StationResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationCreateValue createValue) {
        if (stationDao.existsBy(createValue.getName())) {
            throw new IllegalArgumentException("이미 등록된 지하철역 입니다.");
        }
        return StationResponse.from(stationDao.save(createValue.toEntity()));
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
