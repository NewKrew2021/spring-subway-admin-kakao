package subway.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.line.dao.LineDao;
import subway.line.domain.Line;
import subway.line.domain.LineRequest;
import subway.line.domain.LineResponse;
import subway.section.domain.Section;
import subway.section.domain.SectionRequest;
import subway.section.service.SectionService;
import subway.station.domain.StationResponse;
import subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    public static final int NOT_EXIST = 0;
    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    @Autowired
    public LineService(LineDao lineDao, StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse saveLineAndSection(LineRequest lineRequest) {
        if (lineDao.existByName(lineRequest.getName()) != NOT_EXIST) {
            throw new IllegalArgumentException("중복되는 이름입니다.");
        }
        Line newLine = lineDao.save(Line.of(lineRequest));
        sectionService.save(Section.of(newLine.getId(), lineRequest));
        return LineResponse.of(newLine);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        lineDao.update(Line.of(id, lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void addSectionToLine(Long lindId, SectionRequest sectionRequest) {
        Line line = Line.of(lindId, sectionService.getSectionsByLineId(lindId));
        Section newSection = Section.of(lindId, sectionRequest);

        line.checkAddSectionException(newSection);

        if (line.isEndPointSection(newSection)) {
            sectionService.save(newSection);
            return;
        }
        addInMiddle(line, newSection);
    }


    private void addInMiddle(Line line, Section newSection) {
        Section matchedUpSection = line.sameUpSationId(newSection.getUpStationId());

        if (matchedUpSection != null) {
            checkDistance(matchedUpSection.getDistance(), newSection.getDistance());
            sectionService.save(newSection);
            sectionService.update(matchedUpSection.getId(), Section.of(
                    matchedUpSection.getId(),
                    matchedUpSection.getLineId(),
                    newSection.getDownStationId(),
                    matchedUpSection.getDownStationId(),
                    matchedUpSection.getDistance() - newSection.getDistance()));
            return;
        }

        Section matchedDownSection = line.sameDownSationId(newSection.getDownStationId());

        checkDistance(matchedDownSection.getDistance(), newSection.getDistance());
        sectionService.save(newSection);
        sectionService.update(matchedDownSection.getId(), Section.of(
                matchedDownSection.getId(),
                matchedDownSection.getLineId(),
                matchedDownSection.getUpStationId(),
                newSection.getUpStationId(),
                matchedDownSection.getDistance() - newSection.getDistance()));
    }

    private void checkDistance(int originDistance, int newDistance) {
        if (originDistance <= newDistance) {
            throw new RuntimeException("새로 추가할 구간의 거리가 더 큽니다.");
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public Line findById(Long lineId) {
        return lineDao.findById(lineId);
    }

    public List<StationResponse> getStations(Long id) {
        Line line = Line.of(id, sectionService.getSectionsByLineId(id));
        return line.getStationIds(id).stream()
                .map(stationService::findById)
                .collect(Collectors.toList());
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
        sectionService.deleteSectionByLineId(id);
    }
}
