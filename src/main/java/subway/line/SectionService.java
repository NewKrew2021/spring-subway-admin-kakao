package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import subway.station.StationDao;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section getOneByLineIdAndStationId(@PathVariable Long lineId, @RequestParam("stationId") Long stationId, boolean b) {
        return sectionDao.findOneByLineIdAndStationId(lineId, stationId, b);
    }

    public Section getSection(Line line, Long upStationId, Long downStationId, int distance) {
        return new Section(line.getId(),
                stationDao.findOne(upStationId),
                stationDao.findOne(downStationId),
                distance);
    }
}
