package subway.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.EntityNotFoundException;
import subway.line.dao.LineDao;
import subway.line.domain.Line;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.section.service.SectionService;
import subway.section.domain.Sections;
import subway.station.domain.Stations;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;

    @Autowired
    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse insert(LineRequest request) {
        validateName(request.getName());

        Line newLine = lineDao.insert(request.getName(), request.getColor());

        Sections sections = sectionService.insertOnCreateLine(newLine.getId(), request);
        Stations Stations = sectionService.getStations(sections);

        return newLine.toDto(Stations);
    }

    public void delete(Long lineId) {
        boolean isDeleted = lineDao.delete(lineId);

        if (!isDeleted) {
            throw new EntityNotFoundException("삭제하려는 노선이 존재하지 않습니다.");
        }
    }

    public void update(Long id, LineRequest lineRequest) {
        boolean isUpdated = lineDao.update(id, lineRequest);

        if (!isUpdated) {
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

    private void validateName(String name) {
        if (lineDao.countByName(name) > 0) {
            throw new IllegalArgumentException("이미 등록된 노선 입니다.");
        }
    }
}
