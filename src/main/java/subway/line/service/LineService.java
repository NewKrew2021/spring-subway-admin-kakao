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
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Line insert(Line line, Long upStationId, Long downStationId, int distance) {
        if (lineDao.findLineByName(line.getName()).size() > 0) {
            throw new LineAlreadyExistException();
        }
        Line newLine = lineDao.save(line);
        Sections sections = new Sections(sectionDao.showAllByLineId(newLine.getId()));
        sections.addSection(new Section(newLine.getId(), upStationId, downStationId, distance));
        sectionDao.deleteByLineId(newLine.getId());
        sectionDao.saveAll(sections.getSections());
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
        Sections sections = new Sections(sectionDao.showAllByLineId(line.getId()));
        Sections sortedSection = new Sections(sections.sort(line.getId(), sections.getUpStationAndDownStation(), sections.getDownStationAndDistance()));

        List<Long> stationIds = sortedSection.getStationIds();
        stationIds.add(sortedSection.getLastSectionDownStationId());
        return stationIds.stream()
                .map(stationDao::findStationById)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
        sectionDao.deleteByLineId(id);
    }
}
