package subway.line.service;

import org.springframework.stereotype.Service;
import subway.line.dao.LineDao;
import subway.line.dao.SectionDao;
import subway.line.domain.*;
import subway.station.domain.Station;
import subway.station.dao.StationDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionService sectionService;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionService sectionService, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionService = sectionService;
        this.sectionDao = sectionDao;
    }

    public Line insert(Line line, Long upStationId, Long downStationId, int distance) {
        if (lineDao.findLineByName(line.getName()).size() > 0) {
            throw new LineAlreadyExistException();
        }
        Line newLine = lineDao.save(line);
        sectionService.insert(new Section(newLine.getId(), upStationId, downStationId, distance));
        return newLine;
    }

    public Line findById(Long lineId) {
        return Optional.ofNullable(lineDao.findLineById(lineId))
                .orElseThrow(() -> new LineNotFoundException(lineId));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void updateById(Long lineId, Line line) {
        lineDao.updateById(lineId, line);
    }

    public List<Station> getStations(Line line) {
        Sections sections = new Sections(sectionService.showAll(line.getId()));
        List<Long> stationIds = sections.getStationIds();
        stationIds.add(sections.getLastSectionDownStationId());
        return stationIds.stream()
                .map(stationDao::findStationById)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
        sectionDao.deleteByLineId(id);
    }
}
