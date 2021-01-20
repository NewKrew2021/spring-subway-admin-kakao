package subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import subway.line.dao.LineDao;
import subway.line.vo.Line;
import subway.line.vo.Lines;

@Service
@Transactional
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;

    public LineServiceImpl(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Override
    public Line create(Line line) {
        return lineDao.insert(line);
    }

    @Override
    public Line findLineById(Long id) {
        return lineDao.findLineById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다."));
    }

    @Override
    public Lines findAllLines() {
        return lineDao.findAllLines();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void update(Line line) {
        if (isNotExist(line.getId())) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }

        if (isNotUpdated(lineDao.update(line))) {
            throw new IllegalStateException("지하철 노선을 수정할 수 없습니다.");
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void delete(Long id) {
        if (isNotExist(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }

        if (isNotUpdated(lineDao.delete(id))) {
            throw new IllegalStateException("지하철 노선을 삭제할 수 없습니다.");
        }
    }

    private boolean isNotExist(Long id) {
        return !lineDao.findLineById(id).isPresent();
    }

    private boolean isNotUpdated(int update) {
        return update == 0;
    }
}
