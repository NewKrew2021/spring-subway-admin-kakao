package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.StationResponse;
import subway.exceptions.DuplicateNameException;

import java.util.ArrayList;
import java.util.List;

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

        Station upStation = stationDao.findById(section.getUpStation().getId());
        Station downStation = stationDao.findById(section.getDownStation().getId());

        sectionDao.save(Section.of(newLine, upStation, upStation, Line.INF, Line.HEAD));
        sectionDao.save(Section.of(newLine, upStation, downStation, section.getDistance(), Line.USE));
        sectionDao.save(Section.of(newLine, downStation, downStation, Line.INF, Line.TAIL));

        return newLine;
    }

    public List<Line> showLines() {
        return lineDao.findAll();
    }

    public Line showLine(Long id) {
        return lineDao.findById(id);
    }

    public void modify(Long id, Line line) {
        if (lineDao.countByName(line.getName()) != 0){
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
        lineDao.modify(id, line);
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }


    public List<StationResponse> getSortedStations(Long id) {
        Line line = lineDao.findById(id);

        Sections sections = new Sections(sectionDao.findSectionsByLineId(line.getId()));

        Section currentSection = sections.findHeadSection();

        List<Station> stations = new ArrayList<>();
        while (!currentSection.isEndType()) {
            stations.add(stationDao.findById(currentSection.getDownStation().getId()));
            currentSection = sections.findNextSection(currentSection);
        }


        return StationResponse.listOf(stations);
    }

}
