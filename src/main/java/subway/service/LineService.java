package subway.service;

import org.springframework.stereotype.Service;
import subway.exceptions.DuplicateNameException;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationResponse;

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

    public Line create(Section section) {
        Line line = section.getLine();
        if (lineDao.countByName(line.getName()) != 0){
            throw new DuplicateNameException("노선의 이름은 중복될 수 없습니다.");
        }

        Line newLine = lineDao.save(line);

        Station upStation = stationDao.findById(section.getUpStation().getId()).get();
        Station downStation = stationDao.findById(section.getDownStation().getId()).get();

        sectionDao.save(new Section(newLine, new Station(Line.HEAD, Line.TERMINAL_NAME), upStation, Line.INF));
        sectionDao.save(new Section(newLine, upStation, downStation, section.getDistance()));
        sectionDao.save(new Section(newLine, downStation, new Station(Line.TAIL, Line.TERMINAL_NAME), Line.INF));

        return newLine;
    }

    public List<Line> showLines() {
        return lineDao.findAll()
                .stream()
                .collect(Collectors.toList());
    }

    public Line showLine(Long id) {
        return lineDao.findById(id);
    }

    public void modify(Long id, Line line) {
        if (lineDao.countByName(line.getName()) != 0){
            throw new IllegalArgumentException();
        }
        lineDao.modify(id, line);
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }


    public List<StationResponse> getSortedStations(Long id) {
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
