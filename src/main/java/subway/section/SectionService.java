package subway.section;

import org.springframework.stereotype.Service;
import subway.line.LineDao;

@Service
public class SectionService {
    private LineDao lineDao;
    private SectionDao sectionDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao){
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public void createSection(Long lineId, SectionRequest sectionRequest){
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section section = sections.addSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());

        sectionDao.save(section);
    }

    public void deleteSection(Long lineId, Long stationId){
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.checkDeleteValidationSection();
        sectionDao.deleteById(stationId);
    }

}
