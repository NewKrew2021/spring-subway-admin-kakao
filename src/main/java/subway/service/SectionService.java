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

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section createSection(long stationId, int distance, long lineId) {
        return sectionDao.save(new Section(stationId, distance, lineId));
    }

    public List<Station> getStationsOfLine(long lineId) {
        return sectionDao.findAllStationsByLineId(lineId)
                .stream()
                .map(Section::getStationId)
                .map(stationDao::findById)
                .collect(Collectors.toList());
    }


    @Transactional
    public void addSection(long lineId, long upStationId, long downStationId, int distance) {
        Sections sections = makeSections(lineId);
        Section newSection = sections.addSection(upStationId, downStationId, distance);
        sectionDao.save(newSection);
    }

    @Transactional
    public void deleteSection(long lineId, long stationId) {
        Sections sections = makeSections(lineId);
        sections.deletable(stationId);
        sectionDao.deleteByStationId(stationId);
    }

    public void deleteAllSectionsOfLine(long lineId) {
        sectionDao.deleteAllSectionsByLineId(lineId);
    }

    private Sections makeSections(long lineId) {
        return new Sections(sectionDao.findAllStationsByLineId(lineId));
    }

}
