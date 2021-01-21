package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.section.Section;
import subway.section.SectionRequest;
import subway.section.SectionService;
import subway.section.Sections;
import subway.station.StationResponse;
import subway.station.StationService;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private LineDao lineDao;
    private SectionService sectionService;
    private StationService stationService;

    public LineService(LineDao lineDao, SectionService sectionService, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public Line save(LineRequest lineRequest) {
        lineRequest.checkLineRequest();
        Line newLine = lineDao.save(lineRequest.toLine());
        Section section = lineRequest.toSection(newLine.getId());
        sectionService.save(section);
        return findById(newLine.getId());
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
        sectionService.deleteAllByLineId(id);
    }

    public Line updateLine(Long id, LineRequest lineRequest) {
        Line line = lineDao.findById(id);
        Line newLine = lineRequest.toLine();
        line.updateLine(newLine);
        return lineDao.updateById(line);
    }

    public List<StationResponse> getStationResponsesById(Long lineId) {
        Line line = lineDao.findById(lineId);
        List<StationResponse> responses = new ArrayList<>();
        Sections sections = new Sections(sectionService.getSectionsByLineId(line.getId()), line.getStartStationId());
        for (Long stationId : sections.getStationsSortedSequence()) {
            responses.add(StationResponse.of(stationService.findById(stationId)));
        }
        return responses;
    }

    public Line saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineDao.findById(lineId);
        if (line.isStartStation(sectionRequest.getDownStationId()) || line.isEndStation(sectionRequest.getUpStationId())) {
            return saveSectionsHeadOrTail(sectionRequest.toSection(lineId), line);
        }

        sectionService.save(line, sectionRequest);
        return lineDao.findById(lineId);
    }

    private Line saveSectionsHeadOrTail(Section newSection, Line line) {
        Section newStartEndSection = findUpdateSection(newSection, line);
        line.updateLine(newStartEndSection);
        lineDao.updateById(line);
        sectionService.save(newSection);
        return lineDao.findById(newSection.getLineId());
    }

    private Section findUpdateSection(Section newSection, Line line) {
        if (line.isStartStation(newSection.getDownStationId())) {
            return new Section(newSection.getUpStationId(), line.getEndStationId());
        }
        return new Section(line.getStartStationId(), newSection.getDownStationId());
    }

    @Transactional
    public void deleteStationById(Long lineId, Long stationId) {
        Line line = lineDao.findById(lineId);
        Section newStartEndSection = sectionService.deleteSectionByStationId(line, stationId);
        if (newStartEndSection != null) {
            line.updateLine(newStartEndSection);
            lineDao.updateById(line);
        }
    }
}
