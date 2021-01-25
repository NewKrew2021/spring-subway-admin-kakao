package subway.line.service;

import org.springframework.stereotype.Service;
import subway.line.dao.SectionDao;
import subway.line.domain.Section;
import subway.line.domain.SectionStatus;
import subway.line.domain.Sections;

import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public List<Section> showAll(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        return sections.sort(lineId, sections.getUpStationAndDownStation(), sections.getDownStationAndDistance());
    }

    public void insert(Section section) {
        section.checkValidInsert(showAll(section.getLineId()));

        Sections sections = new Sections(sectionDao.findByLineId(section.getLineId()));
        Map<Long, Long> upStationAndDownStation = sections.getUpStationAndDownStation();

        if (SectionStatus.getSectionStatus(upStationAndDownStation, section) == SectionStatus.UP_STATION_MATCHING) {
            updateWhenUpStationMatching(section);
            return;
        }
        if (SectionStatus.getSectionStatus(upStationAndDownStation, section) == SectionStatus.DOWN_STATION_MATCHING) {
            updateWhenDownStationMatching(section);
            return;
        }
        sectionDao.insert(section);
    }

    private void updateWhenUpStationMatching(Section section) {
        Section upStationMatchSection = sectionDao.findByUpStationId(section.getUpStationId());
        int newDistance = upStationMatchSection.getInsertNewDistance(section);
        sectionDao.insert(section);
        sectionDao.update(new Section(upStationMatchSection.getId(), section.getLineId(), section.getDownStationId(), upStationMatchSection.getDownStationId(), newDistance));
    }

    private void updateWhenDownStationMatching(Section section) {
        Section downStationMatchSection = sectionDao.findByDownStationId(section.getDownStationId());
        int newDistance = downStationMatchSection.getInsertNewDistance(section);
        sectionDao.insert(section);
        sectionDao.update(new Section(downStationMatchSection.getId(), section.getLineId(), downStationMatchSection.getUpStationId(), section.getUpStationId(), newDistance));
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section upStationMatch = sections.getUpStationMatch(stationId);
        Section downStationMatch = sections.getDownStationMatch(stationId);

        sections.checkValidDelete(stationId);

        if (sections.isMiddleStation(stationId)) {
            deleteWhenMiddleSection(lineId, upStationMatch, downStationMatch);
        }
        if (sections.isTerminalStation(stationId)) {
            sectionDao.delete((upStationMatch == null) ? downStationMatch : upStationMatch);
        }
    }

    private void deleteWhenMiddleSection(Long lineId, Section upStationMatch, Section downStationMatch) {
        int newDistance = upStationMatch.getDeleteNewDistance(downStationMatch);

        Section newSection = new Section(lineId, downStationMatch.getUpStationId(), upStationMatch.getDownStationId(), newDistance);
        sectionDao.insert(newSection);

        sectionDao.delete(upStationMatch);
        sectionDao.delete(downStationMatch);
    }
}
