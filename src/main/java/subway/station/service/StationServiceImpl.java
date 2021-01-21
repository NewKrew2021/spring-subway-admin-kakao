package subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import subway.common.exception.NotDeletableEntityException;
import subway.common.exception.NotExistEntityException;
import subway.common.exception.NotUpdatableEntityException;
import subway.station.dao.StationDao;
import subway.station.entity.Station;
import subway.station.entity.Stations;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StationServiceImpl implements StationService {
    private final StationDao stationDao;

    public StationServiceImpl(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public Station create(Station station) {
        return stationDao.insert(station);
    }

    @Override
    @Transactional(readOnly = true)
    public Station findStationById(Long id) {
        return stationDao.findStationById(id)
                .orElseThrow(() -> new NotExistEntityException("존재하지 않는 지하철 역입니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Stations findStationsByIds(List<Long> ids) {
        List<Station> stations = new ArrayList<>();
        for (Long id : ids) {
            stations.add(findStationById(id));
        }
        return new Stations(stations);
    }

    @Override
    @Transactional(readOnly = true)
    public Stations findAllStations() {
        return stationDao.findAllStations();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void update(Station station) {
        if (isNotExist(station.getId())) {
            throw new NotExistEntityException("존재하지 않는 지하철 역입니다.");
        }

        if (isNotUpdated(stationDao.update(station))) {
            throw new NotUpdatableEntityException("지하철 역을 수정할 수 없습니다.");
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void delete(Long id) {
        if (isNotExist(id)) {
            throw new NotExistEntityException("존재하지 않는 지하철 역입니다.");
        }

        if (isNotUpdated(stationDao.delete(id))) {
            throw new NotDeletableEntityException("지하철 역을 삭제할 수 없습니다.");
        }
    }

    private boolean isNotExist(Long id) {
        return !stationDao.findStationById(id).isPresent();
    }

    private boolean isNotUpdated(int update) {
        return update == 0;
    }
}
