package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import subway.exception.ExistLineSaveException;
import subway.section.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Long requestToLine(LineDto lineDto) {
        if( lineDao.hasLineName(lineDto.getName()) ) {
            new ExistLineSaveException().printStackTrace();
        }
        Line line = new Line(lineDto.getName(), lineDto.getColor());
        return lineDao.save(line);
    }
}
