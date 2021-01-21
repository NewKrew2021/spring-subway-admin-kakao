package subway.line;

import org.springframework.stereotype.Service;
import subway.exceptions.BadRequestException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.station.StationDao;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line saveLine(LineRequest lineRequest){
        Line savedLine = lineDao.save(lineRequest);
        sectionDao.save(new Section(savedLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()));
        return savedLine;
    }

    public List<Line> findAll(){
        return lineDao.findAll();
    }

    public Line findById(Long id){
        return lineDao.findById(id);
    }

    public void deleteById(Long id){
        lineDao.deleteById(id);
    }

    public void update(Long id, LineRequest lineRequest){
        lineDao.update(id, lineRequest);
    }
}
