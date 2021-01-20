package subway.section;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import subway.line.LineDao;
import subway.line.LineRequest;
import subway.station.StationDao;

@Service
public class SectionService {

    private final SectionDao sectionDao;


    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public boolean insert(Long lineId, SectionRequest request) {
        return sectionDao.insert(
                lineId,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );
    }

    public boolean delete(Long lineId, Long stationId) {
        return sectionDao.delete(lineId, stationId);
    }

    public Sections findByLineId(Long id) {
        return sectionDao.findByLineId(id);
    }

    public boolean insertOnCreateLine(Long lineId, LineRequest request) {
        return sectionDao.insertOnCreateLine(
                lineId,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );
    }
}
