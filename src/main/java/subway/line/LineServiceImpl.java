package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionService;
import subway.section.Sections;
import subway.station.Station;
import subway.station.StationResponse;
import subway.station.StationService;
import subway.station.StationServiceImpl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationService stationService;

    public LineServiceImpl(LineDao lineDao, SectionDao sectionDao, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Line save(Line line, Section section) {
        Line newLine = lineDao.save(line);
        if (newLine != null) {
            sectionDao.save(new Section(section.getUpStationId(), section.getDownStationId(), section.getDistance(), newLine.getId()));
        }
        return newLine;
    }

    public boolean deleteById(Long lineId) {
        return lineDao.deleteById(lineId) != 0;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findOne(Long lineId) {
        return lineDao.findOne(lineId);
    }

    public boolean update(Line line) {
        return lineDao.update(line) != 0;
    }

    public boolean updateAll(Line line) {
        return lineDao.updateAll(line) != 0;
    }

    public LineResponse saveAndResponse(LineRequest lineRequest) {
        Line line = save(new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId()),
                new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        return new LineResponse(line);
    }

    public List<LineResponse> findAllResponse() {
        return findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findOneResponse(Long lineId) {
        Line line = findOne(lineId);
        Sections sections = sectionDao.getSectionsByLineId(lineId);
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
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

}
