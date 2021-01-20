package subway.section;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import subway.line.LineDao;
import subway.line.LineRequest;
import subway.station.StationDao;
import subway.station.Stations;

import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

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

    public boolean insert(Long lineId, SectionRequest request) {
        validateStations(request.getUpStationId(), request.getDownStationId());
        validateDistance(request.getDistance());

        return sectionDao.insert(
                lineId,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );
    }

    public boolean delete(Long lineId, Long stationId) {
        validateMinimumSections(lineId);
        return sectionDao.delete(lineId, stationId);
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
