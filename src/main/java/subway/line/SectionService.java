package subway.line;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public List<Section> showAll(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Map<Long, Long> upStationAndDownStation = sections.getUpStationAndDownStation();
        Map<Long, Integer> downStationAndDistance = sections.getDownStationAndDistance();

        return sections.sort(lineId, upStationAndDownStation, downStationAndDistance);
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

    public int delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section upStationMatch = sections.getUpStationMatch(stationId);
        Section downStationMatch = sections.getDownStationMatch(stationId);

        sections.checkValidDelete(stationId);

        if (isMiddleStation(upStationMatch, downStationMatch)) {
            return deleteWhenMiddleSection(lineId, upStationMatch, downStationMatch);
        }
        if (isTerminalStation(upStationMatch, downStationMatch)) {
            return sectionDao.delete((upStationMatch == null) ? downStationMatch : upStationMatch);
        }

        return 0;
    }

    private int deleteWhenMiddleSection(Long lineId, Section upStationMatch, Section downStationMatch) {
        int deleteCount = 0;
        int newDistance = upStationMatch.getDeleteNewDistance(downStationMatch);

        Section newSection = new Section(lineId, downStationMatch.getUpStationId(), upStationMatch.getDownStationId(), newDistance);
        sectionDao.insert(newSection);

        deleteCount += sectionDao.delete(upStationMatch);
        deleteCount += sectionDao.delete(downStationMatch);

        return deleteCount;
    }

    private boolean isTerminalStation(Section upStationMatch, Section downStationMatch) {
        return upStationMatch != null ^ downStationMatch != null;
    }

    private boolean isMiddleStation(Section upStationMatch, Section downStationMatch) {
        return upStationMatch != null && downStationMatch != null;
    }
}
