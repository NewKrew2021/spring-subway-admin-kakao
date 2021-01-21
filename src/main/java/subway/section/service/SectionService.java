package subway.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.exception.EntityNotFoundException;
import subway.line.domain.LineRequest;
import subway.section.dao.SectionDao;
import subway.section.domain.SectionRequest;
import subway.section.domain.Sections;
import subway.station.dao.StationDao;
import subway.station.domain.Stations;

import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    @Autowired
    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Sections insertOnCreateLine(Long lineId, LineRequest request) {
        validateStations(request.getUpStationId(), request.getDownStationId());
        validateDistance(request.getDistance());

        return sectionDao.insertOnCreateLine(
                lineId,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );
    }

    public void insert(Long lineId, SectionRequest request) {
        validateStations(request.getUpStationId(), request.getDownStationId());
        validateDistance(request.getDistance());

        sectionDao.insert(
                lineId,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );
    }

    public void delete(Long lineId, Long stationId) {
        validateMinimumSections(lineId);
        boolean isDeleted = sectionDao.delete(lineId, stationId);

        if (!isDeleted) {
            throw new EntityNotFoundException("삭제하려는 구간이 존재하지 않습니다.");
        }
    }

    public Sections findByLineId(Long id) {
        return sectionDao.findByLineId(id);
    }

    public Stations getStations(Sections sections) {
        return new Stations(
                sections.getStationIds()
                        .stream()
                        .map(stationId -> stationDao.findById(stationId))
                        .collect(Collectors.toList())
        );
    }

    private void validateStations(Long upStationId, Long downStationId) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException("구간의 두 역이 같습니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("구간의 길이는 0보다 커야합니다.");
        }
    }

    private void validateMinimumSections(Long lineId) {
        if (sectionDao.countByLineId(lineId) <= 2) {
            throw new IllegalArgumentException("노선에 역이 두 개밖에 없습니다.");
        }
    }
}
