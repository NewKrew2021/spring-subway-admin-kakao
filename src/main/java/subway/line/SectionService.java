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
        Map<Long, Long> upStationAndDownStation = sectionDao.findByLineId(lineId).stream()
                .collect(Collectors.toMap(Section::getUpStationId, Section::getDownStationId));
        Map<Long, Integer> downStationAndDistance = sectionDao.findByLineId(lineId).stream()
                .collect(Collectors.toMap(Section::getDownStationId, Section::getDistance));

        return order(lineId, upStationAndDownStation, downStationAndDistance);
    }

    private List<Section> order(Long lineId, Map<Long, Long> upStationAndDownStation, Map<Long, Integer> downStationAndDistance) {
        Long startId = upStationAndDownStation.keySet().stream()
                .filter(stationId -> !upStationAndDownStation.containsValue(stationId))
                .findFirst()
                .orElse(0L);
        List<Section> result = new ArrayList<>();

        while (result.size() != upStationAndDownStation.size()) {
            result.add(new Section(lineId, startId, upStationAndDownStation.get(startId),
                    downStationAndDistance.get(upStationAndDownStation.get(startId))));
            startId = upStationAndDownStation.get(startId);
        }
        return result;
    }

    public void insert(Section section) {
        section.checkValidInsert(showAll(section.getLineId()));

        Map<Long, Long> upStationAndDownStation = sectionDao.findByLineId(section.getLineId()).stream()
                .collect(Collectors.toMap(Section::getUpStationId, Section::getDownStationId));
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
        List<Section> sections = sectionDao.findByLineId(lineId);
        Section upStationMatch = sections.stream()
                .filter(section1 -> section1.getUpStationId() == stationId)
                .findFirst()
                .orElse(null);
        Section downStationMatch = sections.stream()
                .filter(section1 -> section1.getDownStationId() == stationId)
                .findFirst()
                .orElse(null);
        if (isNotValidDelete(sections, upStationMatch, downStationMatch)) {
            throw new IllegalArgumentException();
        }

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

    private boolean isNotValidDelete(List<Section> sections, Section upStationMatch, Section downStationMatch) {
        return sections.size() == 1 && (upStationMatch != null || downStationMatch != null);
    }

    private boolean isTerminalStation(Section upStationMatch, Section downStationMatch) {
        return upStationMatch != null ^ downStationMatch != null;
    }

    private boolean isMiddleStation(Section upStationMatch, Section downStationMatch) {
        return upStationMatch != null && downStationMatch != null;
    }
}
