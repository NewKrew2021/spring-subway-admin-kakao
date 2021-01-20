package subway.line;

import org.springframework.stereotype.Service;
import subway.exceptions.lineExceptions.LineDuplicatedException;
import subway.exceptions.lineExceptions.LineNotFoundException;
import subway.section.SectionService;

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
        Long newLineId = lineDao.save(lineRequest).getId();
        sectionService.lineInitialize(newLineId, lineRequest);
        return makeLineResponseByLine(lineDao.findById(newLineId));
    }

    public List<LineResponse> getLineResponses() {
        return lineDao.findAll()
                .stream()
                .map(this::makeLineResponseByLine)
                .collect(Collectors.toList());
    }

    public LineResponse showLineById(Long lineId) {
        lineFindValidate(lineId);
        return makeLineResponseByLine(lineDao.findById(lineId));
    }

    private LineResponse makeLineResponseByLine(Line line) {
        return LineResponse.of(line, sectionService.findSortedStationsByLineId(line.getId()));
    }

    public void deleteLineById(Long lineId) {
        lineFindValidate(lineId);
        lineDao.deleteById(lineId);
    }

    public void updateLineByLineId(Long lineId, LineRequest lineRequest) {
        lineFindValidate(lineId);
        lineDao.update(lineId, lineRequest.getName(), lineRequest.getColor());
    }

    private void lineFindValidate(Long lineId) {
        if (lineDao.findById(lineId) == null) {
            throw new LineNotFoundException();
        }
    }

}
