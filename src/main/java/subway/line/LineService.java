package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.NotExistException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        List<StationResponse> stations = getStartAndEndStationResponse(lineRequest.getUpStationId(), lineRequest.getDownStationId());

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId());
        Line newLine = lineDao.save(line);

        Section section = new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance(), newLine.getId());
        sectionDao.save(section);

        return newLine.makeLineResponse(stations);
    }

    private List<StationResponse> getStartAndEndStationResponse(Long upStationId, Long downStationId) {
        List<StationResponse> stations = new ArrayList<>();
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);
        stations.add(upStation.makeStationResponse());
        stations.add(downStation.makeStationResponse());
        return stations;
    }

    public boolean existName(String name) {
        return lineDao.countByName(name) != 0;
    }

    public void deleteLine(long id) {
        lineDao.deleteById(id);
    }

    public void updateLine(long id, LineRequest lineRequest) {
        Line originalLine = lineDao.findById(id);
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor(), originalLine.getStartStationId(), originalLine.getEndStationId());
        lineDao.updateById(id, line);
    }

    public List<LineResponse> getAllLines() {
        return lineDao.findAll().stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), null))
                .collect(Collectors.toList());
    }

    public Line getLine(long id) {
        Line line = lineDao.findById(id);
        if (line == null) {
            throw new NotExistException("해당 노선이 존재하지 않습니다.");
        }
        return line;
    }
}
