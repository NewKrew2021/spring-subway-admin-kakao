package subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.line.factory.LineFactory;
import subway.line.dao.LineDao;
import subway.line.domain.Line;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.line.exception.LineAlreadyExistException;
import subway.section.domain.Section;
import subway.section.dao.SectionDao;
import subway.section.domain.Sections;
import subway.station.dao.StationDao;
import subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse insertLine(LineRequest lineRequest) {
        Line line = LineFactory.makeLine(lineRequest);

        if (hasContain(line.getName())) {
            throw new LineAlreadyExistException();
        }
        Line newLine = lineDao.save(LineFactory.makeLine(lineRequest));
        sectionDao.createLineSection(newLine, lineRequest);
        return LineResponse.of(newLine);
    }

    public void update(Line line) {
        lineDao.update(line);
    }

    @Transactional
    public void delete(long id) {
        lineDao.deleteLineById(id);
        List<Section> list = sectionDao.getSections(id);
        list.forEach(section -> sectionDao.delete(section.getLineId(), section.getStationId()));
    }

    public boolean hasContain(String name) {
        return lineDao.findByName(name) == 1;
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> new LineResponse(line, null))
                .collect(Collectors.toList());
    }

    public LineResponse findById(long lineId) {
        Line line = lineDao.findById(lineId); // 이 단계에서 station id는 line이 갖고 있으나, station 에 각각에 대한 정보는 없다.
        Sections sections = new Sections(sectionDao.getSections(lineId));

        List<Long> stationsId = sections.getStationsId();
        List<StationResponse> stationResponses = stationsId
                .stream()
                .map(stationDao::findById)
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return new LineResponse(line, stationResponses);
    }
}
