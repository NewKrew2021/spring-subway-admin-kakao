package subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import subway.common.exception.NotDeletableEntityException;
import subway.common.exception.NotExistEntityException;
import subway.common.exception.NotUpdatableEntityException;
import subway.line.dao.LineDao;
import subway.line.dto.LineResponse;
import subway.line.entity.Line;
import subway.line.entity.Lines;
import subway.section.service.SectionService;
import subway.station.entity.Stations;
import subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineServiceImpl implements LineService {
    private final StationService stationService;
    private final SectionService sectionService;
    private final LineDao lineDao;

    public LineServiceImpl(StationService stationService, SectionService sectionService, LineDao lineDao) {
        this.stationService = stationService;
        this.sectionService = sectionService;
        this.lineDao = lineDao;
    }

    @Override
    public Line create(String name, String color) {
        return lineDao.insert(name, color);
    }

    @Override
    public Line getLineById(Long id) {
        return lineDao.findLineById(id)
                .orElseThrow(() -> new NotExistEntityException("존재하지 않는 지하철 노선입니다."));
    }

    @Override
    public Lines getAllLines() {
        return lineDao.findAllLines()
                .orElseThrow(() -> new NotExistEntityException("지하철 노선이 한개도 존재하지 않습니다."));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void update(Line line) {
        if (isNotExist(line.getId())) {
            throw new NotExistEntityException("존재하지 않는 지하철 노선입니다.");
        }

        if (isNotUpdated(lineDao.update(line))) {
            throw new NotUpdatableEntityException("지하철 노선을 수정할 수 없습니다.");
        }
    }

    private boolean isNotExist(Long id) {
        return !lineDao.findLineById(id).isPresent();
    }

    private boolean isNotUpdated(int update) {
        return update == 0;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void delete(Long id) {
        if (isNotExist(id)) {
            throw new NotExistEntityException("존재하지 않는 지하철 노선입니다.");
        }

        if (isNotUpdated(lineDao.delete(id))) {
            throw new NotDeletableEntityException("지하철 노선을 삭제할 수 없습니다.");
        }
    }

    @Override
    public LineResponse getLineWithStationsByLineId(Long lineId) {
        Line line = getLineById(lineId);
        List<Long> stationIds = sectionService.getSectionsByLineId(lineId)
                .getStationIds();
        Stations stations = stationService.getStationsByIds(stationIds);
        return new LineResponse(line, stations);
    }

    @Override
    public List<LineResponse> getAllLinesWithStations() {
        return getAllLines().stream()
                .map(line -> getLineWithStationsByLineId(line.getId()))
                .collect(Collectors.toList());
    }
}
