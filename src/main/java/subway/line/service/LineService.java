package subway.line.service;

import org.springframework.stereotype.Service;
import subway.exceptions.DuplicateNameException;
import subway.line.dao.LineDao;
import subway.line.domain.Line;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.section.dao.SectionDao;
import subway.section.domain.Section;
import subway.section.domain.Sections;
import subway.station.dao.StationDao;
import subway.station.domain.Station;
import subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;

    public LineService (LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        if (lineDao.countByName(lineRequest.getName()) != 0){
            throw new DuplicateNameException("노선의 이름은 중복될 수 없습니다.");
        }

        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));

        Station upStation = stationDao.findById(lineRequest.getUpStationId()).get();
        Station downStation = stationDao.findById(lineRequest.getDownStationId()).get();

        sectionDao.save(new Section(newLine, new Station(Line.HEAD, Line.TERMINAL_NAME), upStation, Line.INF));
        sectionDao.save(new Section(newLine, upStation, downStation, lineRequest.getDistance()));
        sectionDao.save(new Section(newLine, downStation, new Station(Line.TAIL, Line.TERMINAL_NAME), Line.INF));

        return new LineResponse(newLine, getSortedStations(newLine.getId()));
    }

    public List<LineResponse> showLines() {
        return lineDao.findAll()
                .stream()
                .map(line -> new LineResponse(line, getSortedStations(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line, getSortedStations(id));
    }

    public void modify(Long id, LineRequest lineRequest) {
        if (lineDao.countByName(lineRequest.getName()) != 0){
            throw new IllegalArgumentException();
        }
        lineDao.modify(id, lineRequest);
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }


    private List<StationResponse> getSortedStations(Long id) {
        Line line = lineDao.findById(id);

        Sections sections = new Sections(sectionDao.findSectionsByLineId(line.getId()));

        Section currentSection = sectionDao.findSectionByLineIdAndUpStationId(id, Line.HEAD);

        List<Station> stations = new ArrayList<>();
        while (currentSection.getDownStation().getId() != Line.TAIL) {
            stations.add(stationDao.findById(currentSection.getDownStation().getId()).get());
            currentSection = sections.findNextSection(currentSection);
        }

        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

}
