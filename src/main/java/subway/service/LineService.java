package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
import subway.request.LineRequest;
import subway.response.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Line newLine = lineDao.save(lineRequest.getDomain());
        Section section = new Section(newLine.getId(), lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(section);
        return new LineResponse(newLine, getOrderedStationsOfLine(newLine.getId()));
    }

    public List<LineResponse> getLines() {
        return lineDao.findAll().stream()
                .map(line -> new LineResponse(line, getOrderedStationsOfLine(line.getId())))
                .collect(Collectors.toList());
    }

    public OrderedStations getOrderedStationsOfLine(Long lineId) {
        // 구간들이 있어야 역들의 순서를 맞출 수 있다.
        OrderedSections orderedSections = new OrderedSections(sectionDao.getByLineId(lineId));
        List<Station> stations = stationDao.batchGetByIds(orderedSections.getOrderedStationIds());

        return new OrderedStations(orderedSections, stations);
    }

    public LineResponse getLine(Long id) {
        Line line = lineDao.getById(id);
        OrderedStations stations = getOrderedStationsOfLine(id);
        return new LineResponse(line, stations);
    }

    public boolean modifyLine(Long id, LineRequest lineRequest) {
        return lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    public boolean deleteLine(Long id) {
        sectionDao.deleteAllByLineId(id);
        return lineDao.deleteById(id);
    }
}
