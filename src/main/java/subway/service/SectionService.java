package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.dao.LineDao;
import subway.domain.station.Station;
import subway.dao.StationDao;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void createSection(Section section) {
        sectionDao.save(section);
    }

    public List<Station> getStationsOfLine(long lineId) {
        return sectionDao.findAllStationsByLineId(lineId)
                .stream()
                .map(section -> section.getStationId())
                .map(stationId -> stationDao.findById(stationId))
                .collect(Collectors.toList());
    }


    @Transactional
    public void addSection(long lineId, long upStationId, long downStationId, int distance) {
        Sections sections = makeSections(lineId);
        Section newSection = sections.addSection(upStationId,downStationId,distance);
        sectionDao.save(newSection);
    }

    @Transactional
    public void deleteSection(long lineId, long stationId) {
        Sections sections = makeSections(lineId);
        sections.deletable(stationId);
        sectionDao.deleteByStationId(stationId);
    }

    private Sections makeSections(long lineId) {
        return new Sections(sectionDao.findAllStationsByLineId(lineId));
    }

}
