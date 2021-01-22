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

import java.util.List;

@Service
@Transactional
public class StationServiceImpl implements StationService {
    private final StationDao stationDao;

    public StationServiceImpl(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public Station create(String name) {
        return stationDao.insert(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Station getStationById(Long id) {
        return stationDao.findStationById(id)
                .orElseThrow(() -> new NotExistEntityException("존재하지 않는 지하철 역입니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Stations getStationsByIds(List<Long> ids) {
        Stations stations = stationDao.findStationsByIds(ids)
                .orElseThrow(() -> new NotExistEntityException("일치하는 지하철 역이 한개도 없습니다."));
        if (!stations.hasSameSize(ids.size())) {
            throw new NotExistEntityException("존재하지 않는 지하철 역이 있습니다.");
        }
        return stations.sortByIds(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Stations getAllStations() {
        return stationDao.findAllStations()
                .orElseThrow(() -> new NotExistEntityException("지하철 역이 한개도 존재하지 않습니다."));
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

    private boolean isNotExist(Long id) {
        return !stationDao.findStationById(id).isPresent();
    }

    private boolean isNotUpdated(int update) {
        return update == 0;
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
}
