package subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.line.Line;
import subway.line.LineDao;
import subway.station.Station;
import subway.station.StationDao;

import java.util.List;

@Service
public class SectionService {
    SectionDao sectionDao;
    StationDao stationDao;
    LineDao lineDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public void save(Section newSection) {
        SectionsInLine sectionsInLine = sectionDao.findAllByLine(newSection.getLine(), stationDao, lineDao);

        validateSave(sectionsInLine, newSection);

        Section containingSection = sectionsInLine.findContainingExistingSection(newSection);

        if(containingSection == null) {
            sectionDao.save(newSection);
            return;
        }

        Section splitSection = containingSection.splitBy(newSection);
        sectionDao.save(splitSection);
        sectionDao.save(newSection);
        sectionDao.deleteById(containingSection.getId());
    }

    @Transactional
    public void deleteStation(Line line, Station station) {
        SectionsInLine sectionsInLine = sectionDao.findAllByLine(line, stationDao, lineDao);
        validateDeletion(sectionsInLine, station);

        if(sectionsInLine.ofTerminalStationIs(station)) {
            Section terminalSection = sectionsInLine.findSectionByStation(station);
            sectionDao.deleteById(terminalSection.getId());
            return;
        }

        Section upwardSection = sectionsInLine.findUpwardSectionByStation(station);
        Section downwardSection = sectionsInLine.findDownWardSectionByStation(station);
        Section connectedSection = upwardSection.connectDownward(downwardSection);
        sectionDao.save(connectedSection);
        sectionDao.deleteById(upwardSection.getId());
        sectionDao.deleteById(downwardSection.getId());
    }

    public List<Station> findSortedStationsByLine(Line line) {
        SectionsInLine sectionsInLine = sectionDao.findAllByLine(line, stationDao, lineDao);
        return sectionsInLine.findSortedStations();
    }

    private void validateSave(SectionsInLine sectionsInLine, Section newSection) {
        if(sectionsInLine.getSize() == 0) return;
        if(!sectionsInLine.containsStation(newSection.getUpStation()) && !sectionsInLine.containsStation(newSection.getDownStation())) {
            throw new RuntimeException();
        }
    }

    private void validateDeletion(SectionsInLine sectionsInLine, Station station) {
        if(sectionsInLine.getSize() <= 1) throw new RuntimeException();
        if(!sectionsInLine.containsStation(station)) throw new RuntimeException();
    }

}
