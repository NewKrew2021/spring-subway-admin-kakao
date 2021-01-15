package subway.line;

import org.springframework.stereotype.Service;

@Service
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;

    public LineServiceImpl(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse save(Line line, Section section){
        Line savedLine = lineDao.save(line, section);
        return new LineResponse(savedLine.getId(), savedLine.getColor(), savedLine.getName());
    }

    public boolean deleteById(Long lineId){
        return lineDao.deleteById(lineId) != 0;
    }
}
