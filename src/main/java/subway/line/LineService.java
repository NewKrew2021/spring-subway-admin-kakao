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

    public Line createLine(Line line, int distance) {
        Line newLine = lineDao.save(line);

        Section section = new Section(newLine.getStartStationId(), newLine.getEndStationId(), distance, newLine.getId());
        sectionDao.save(section);

        return newLine;
    }

    public List<StationResponse> getStartAndEndStationResponse(Long upStationId, Long downStationId) {
        List<StationResponse> stations = new ArrayList<>();
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);
        stations.add(upStation.toResponse());
        stations.add(downStation.toResponse());
        return stations;
    }

    public boolean existName(String name) {
        return lineDao.countByName(name) != 0;
    }

    public void deleteLine(long id) {
        lineDao.deleteById(id);
    }

    public void updateLine(long id, Line line) {
        Line originalLine = lineDao.findById(id);
        lineDao.updateById(id, originalLine.getLineNameAndColorChanged(line.getName(), line.getColor()));
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
