package subway.line;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exceptions.DuplicateLineNameException;
import subway.exceptions.InvalidLineArgumentException;
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

    public static final int ZERO = 0;
    private LineDao lineDao;
    private SectionService sectionService;
    private StationService stationService;

    public LineService(LineDao lineDao, SectionService sectionService, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @Transactional
    public Line save(LineRequest lineRequest) {
        lineRequest.checkLineRequest();
        try {
            Line newLine = lineDao.save(lineRequest.toLine()).orElseThrow(() -> new InvalidLineArgumentException("노선 저장에 오류가 발생했습니다."));
            Section section = lineRequest.toSection(newLine.getId());
            sectionService.save(section);
            return findById(newLine.getId());
        } catch (DuplicateKeyException e) {
            throw new DuplicateLineNameException("노선 이름이 중복되었습니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id).orElseThrow(()-> new InvalidLineArgumentException("해당하는 노선이 존재하지 않습니다."));
    }

    @Transactional
    public void deleteById(Long id) {
        int deletedRow = lineDao.deleteById(id);
        if(deletedRow == ZERO) {
            throw new InvalidLineArgumentException("노선을 삭제하지 못했습니다.");
        }
        sectionService.deleteAllByLineId(id);
    }

    public Line updateLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        Line newLine = lineRequest.toLine();
        line.updateLine(newLine);
        return lineDao.updateById(line).orElseThrow(() -> new InvalidLineArgumentException("노선 정보를 업데이트 하지 못했습니다."));
    }

    public List<StationResponse> getStationResponsesById(Long lineId) {
        Line line = findById(lineId);
        List<StationResponse> responses = new ArrayList<>();
        Sections sections = new Sections(sectionService.getSectionsByLineId(line.getId()), line.getStartStationId());
        for (Long stationId : sections.getStationsSortedSequence()) {
            responses.add(StationResponse.from(stationService.findById(stationId)));
        }
        return responses;
    }

    @Transactional
    public Line saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findById(lineId);
        if (line.isStartStation(sectionRequest.getDownStationId()) || line.isEndStation(sectionRequest.getUpStationId())) {
            return saveSectionsHeadOrTail(sectionRequest.toSection(lineId), line);
        }
        sectionService.save(line, sectionRequest);
        return findById(lineId);
    }

    private Line saveSectionsHeadOrTail(Section newSection, Line line) {
        Section newStartEndSection = findUpdateSection(newSection, line);
        line.updateLine(newStartEndSection);
        lineDao.updateById(line);
        sectionService.save(newSection);
        return findById(line.getId());
    }

    private Section findUpdateSection(Section newSection, Line line) {
        if (line.isStartStation(newSection.getDownStationId())) {
            return new Section(newSection.getUpStationId(), line.getEndStationId());
        }
        return new Section(line.getStartStationId(), newSection.getDownStationId());
    }

    @Transactional
    public void deleteStationById(Long lineId, Long stationId) {
        Line line = findById(lineId);
        Section newStartEndSection = sectionService.deleteSectionByStationId(line, stationId);
        if (newStartEndSection != null) {
            line.updateLine(newStartEndSection);
            lineDao.updateById(line);
        }
    }
}
