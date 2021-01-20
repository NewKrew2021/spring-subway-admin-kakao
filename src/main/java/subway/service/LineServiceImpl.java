package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineResponseWithStation;
import subway.dto.StationResponse;
import subway.exception.DataEmptyException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineServiceImpl(LineDao lineDao, SectionService sectionService, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @Override
    @Transactional
    public Line save(Line line, Section section) {
        Line newLine = lineDao.save(line);
        sectionService.save(new Section(section.getUpStationId(), section.getDownStationId(), section.getDistance(), newLine.getId()));
        return newLine;
    }

    @Override
    @Transactional
    public boolean deleteById(Long lineId) {
        if (lineDao.deleteById(lineId) == 0) {
            return false;
        }
        sectionService.deleteSectionByLineId(lineId);
        return true;
    }

    @Override
    public List<Line> findAll() {
        List<Line> lines = lineDao.findAll();
        if (lines.size() == 0) {
            throw new DataEmptyException();
        }
        return lines;
    }

    @Override
    public Line findOne(Long lineId) {
        Line line = lineDao.findOne(lineId);
        if (line == null) {
            throw new DataEmptyException();
        }
        return line;
    }

    @Override
    public boolean update(Line line) {
        return lineDao.update(line) != 0;
    }

    @Override
    public LineResponseWithStation findOneResponse(Long lineId) {
        Line line = findOne(lineId);
        Sections sections = sectionService.getSectionsByLineId(lineId);
        Set<Long> stationIds = new LinkedHashSet<>();
        sections.getSections()
                .forEach(section -> {
                    stationIds.add(section.getUpStationId());
                    stationIds.add(section.getDownStationId());
                });

        List<StationResponse> stationResponses = stationIds.stream()
                .map(id -> {
                    Station station = stationService.findOne(id);
                    return new StationResponse(station);
                })
                .collect(Collectors.toList());
        return new LineResponseWithStation(line.getId(), line.getName(), line.getColor(), stationResponses);
    }
}
