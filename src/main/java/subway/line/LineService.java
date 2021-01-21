package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.ExistLineSaveException;
import subway.section.*;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Long requestToLine(Line line) {
        if( lineDao.hasLineName(line.getName()) ) {
            new ExistLineSaveException().printStackTrace();
        }
        return lineDao.save(line);
    }
}
