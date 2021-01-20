package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import subway.exception.EntityNotFoundException;
import subway.section.Section;
import subway.section.SectionService;
import subway.section.Sections;
import subway.station.StationService;
import subway.station.Stations;

import java.util.Arrays;
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

    public LineResponse insert(LineRequest request) {

        lineDao.validateName(request.getName());
        Line newLine = lineDao.insert(request.getName(), request.getColor());

        Sections sections = sectionService.insertOnCreateLine(newLine.getId(), request);
        Stations Stations = sectionService.getStations(sections);

        return newLine.toDto(Stations);
    }

    public void delete(Long lineId) {
        boolean deleted = lineDao.delete(lineId);

        if (!deleted) {
            throw new EntityNotFoundException("삭제하려는 노선이 존재하지 않습니다.");
        }
    }

    public void update(Long id, LineRequest lineRequest) {
        boolean updated = lineDao.update(id, lineRequest);

        if (!updated) {
            throw new EntityNotFoundException("수정하려는 노선이 존재하지 않습니다.");
        }
    }

    public Line findById(Long lineId) {
        return lineDao.findById(lineId);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> {
                    Sections sections = sectionService.findByLineId(line.getId());
                    Stations stations = sectionService.getStations(sections);
                    return line.toDto(stations);
                })
                .collect(Collectors.toList());
    }
}
