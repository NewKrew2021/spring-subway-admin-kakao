package subway.line;

import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Line line, LineRequest lineRequest) {
        sectionDao.save(new Section(line.getId(), Line.HEAD, lineRequest.getUpStationId(), Section.VIRTUAL_DISTANCE));
        sectionDao.save(new Section(line.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        sectionDao.save(new Section(line.getId(), lineRequest.getDownStationId(), Line.TAIL, Section.VIRTUAL_DISTANCE));
    }

}
