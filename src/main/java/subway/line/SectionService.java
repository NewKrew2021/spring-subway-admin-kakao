package subway.line;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
        if(SectionStatus.getSectionStatus(upStationAndDownStation, section) == SectionStatus.UP_STATION_MATCHING){
            Section upStationMatchSection = sectionDao.findByUpStationId(section.getUpStationId());
            section.checkValidDistance(upStationMatchSection);
            sectionDao.insert(section);
            int newDistance = upStationMatchSection.getDistance() - section.getDistance();
            sectionDao.update(new Section(upStationMatchSection.getId(), section.getLineId(), section.getDownStationId(), upStationMatchSection.getDownStationId(), newDistance));
            return;
        }
        if(SectionStatus.getSectionStatus(upStationAndDownStation, section) == SectionStatus.DOWN_STATION_MATCHING){
            Section downStationMatchSection = sectionDao.findByDownStationId(section.getDownStationId());
            section.checkValidDistance(downStationMatchSection);
            sectionDao.insert(section);
            int newDistance = downStationMatchSection.getDistance() - section.getDistance();
            sectionDao.update(new Section(downStationMatchSection.getId(), section.getLineId(), downStationMatchSection.getUpStationId(), section.getUpStationId(), newDistance));
            return;
            }
        sectionDao.insert(section);
    }


}
