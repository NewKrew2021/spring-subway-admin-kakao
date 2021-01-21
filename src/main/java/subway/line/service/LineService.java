package subway.line.service;

import org.springframework.stereotype.Service;
import subway.exceptions.lineExceptions.LineDuplicatedException;
import subway.line.domain.Line;
import subway.line.domain.LineDao;
import subway.line.presentation.LineRequest;
import subway.line.presentation.LineResponse;
import subway.section.service.SectionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        if (lineDao.findByName(lineRequest.getName()) != null) {
            throw new LineDuplicatedException();
        }
        Long newLineId = lineDao.save(new Line(lineRequest)).getId();
        sectionService.lineInitialize(lineRequest.toFirstSection(newLineId), lineRequest.toSection(newLineId));
        return makeLineResponseByLine(lineDao.findById(newLineId));
    }

    public List<LineResponse> getLineResponses() {
        return lineDao.findAll()
                .stream()
                .map(this::makeLineResponseByLine)
                .collect(Collectors.toList());
    }

    public LineResponse showLineById(Long lineId) {
        return makeLineResponseByLine(lineDao.findById(lineId));
    }

    private LineResponse makeLineResponseByLine(Line line) {
        return LineResponse.of(line, sectionService.findSortedStationsByLineId(line.getId()));
    }

    public void deleteLineById(Long lineId) {
        lineDao.deleteById(lineId);
    }

    public void updateLineByLineId(Long lineId, LineRequest lineRequest) {
        lineDao.findById(lineId);
        lineDao.update(new Line(lineId,lineRequest));
    }

}
