package subway.line;

import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.LineNotFoundException;
import subway.section.SectionService;
import subway.section.Sections;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public LineResponse create(LineRequest request) {
        if (lineDao.existBy(request.getName())) {
            throw new IllegalArgumentException("이미 등록된 지하철 노선 입니다.");
        }

        Line newLine = lineDao.save(request.toEntity());
        sectionService.save(Sections.createInitialSections(request.getSectionRequest().toEntity(newLine.getId())));
        return LineResponse.from(newLine, sectionService.findStationsOf(newLine.getId()));
    }

    @Transactional(readOnly = true)
    public LineResponse findBy(Long id) {
        Line line = lineDao.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        return LineResponse.from(line, sectionService.findStationsOf(line.getId()));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> LineResponse.from(line, sectionService.findStationsOf(line.getId())))
                .collect(toList());
    }

    public void update(LineRequest request, Long id) {
        try {
            lineDao.update(request.toEntity(id));
        } catch (IncorrectUpdateSemanticsDataAccessException e) {
            throw new LineNotFoundException(id);
        }
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }
}
