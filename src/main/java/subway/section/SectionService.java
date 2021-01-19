package subway.section;

import org.springframework.stereotype.Service;
import subway.line.Line;
import subway.line.LineDao;
import subway.station.StationDao;

@Service
public class SectionService {
    private LineDao lineDao;
    private SectionDao sectionDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao){
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(Long lineId, SectionRequest sectionRequest){
        Line line = lineDao.findById(lineId);
        Section section = new Section(line.getId(),
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        Section newSection = sectionDao.save(section);
        return new SectionResponse(newSection.getUpStationId(),
                newSection.getDownStationId(),
                newSection.getDistance());
    }

    public void deleteSection(Long lineId, Long stationId){
        sectionDao.deleteById(lineId, stationId);

    }

}
