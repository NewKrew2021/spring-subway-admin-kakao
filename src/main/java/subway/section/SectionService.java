package subway.section;

import org.springframework.stereotype.Service;
import subway.exceptions.CannotConstructRightSectionsForLine;
import subway.exceptions.NotFoundException;
import subway.station.Station;
import subway.station.StationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(Long lineId, SectionDto sectionDto) {
        Section section = new Section(lineId, sectionDto);

        if(!canInsert(section)){
            throw new CannotConstructRightSectionsForLine("잘못된 section 저장입니다.");
        }

        /* 중간에 끼워 넣는경우 1 : upstation을 기준으로 기존 section을 삭제하고, 새롭게 연결해주어야 할 section을 추가 */
        if(existSameUpstationId(section.getLineId(), section.getUpStationId())) {
            Section oldSection = sectionDao.getSectionByUpStationId(section.getLineId(), section.getUpStationId());
            addNewBackwardSection(section, oldSection);
            sectionDao.deleteById(oldSection.getId());
        }

        /* 중간에 끼워 넣는 경우 2 : downstation을 기준으로 기존 section을 삭제하고, 새롭게 연결해주어야 할 section을 추가 */
        if(existSameDownStationId(section.getLineId(), section.getDownStationId())) {
            Section oldSection = sectionDao.getSectionByDownStationId(section.getLineId(), section.getDownStationId());
            addNewForwardSection(section, oldSection);
            sectionDao.deleteById(oldSection.getId());
        }

        /* 전달된 새로운 section을 추가 */
        sectionDao.save(section);
    }

    private void addNewBackwardSection(Section section, Section oldSection) {
        Section newSection = new Section(
                section.getDownStationId(),
                oldSection.getDownStationId(),
                section.getLineId(),
                oldSection.getDistance() - section.getDistance()
        );

        sectionDao.save(newSection);
    }

    private void addNewForwardSection(Section section, Section oldSection) {
        Section newSection = new Section(
                oldSection.getUpStationId(),
                section.getUpStationId(),
                section.getLineId(),
                oldSection.getDistance() - section.getDistance()
        );

        sectionDao.save(newSection);
    }

    public boolean existSameUpstationId(Long lineId, Long upStationId) {
        if(sectionDao.getSectionByUpStationId(lineId, upStationId) != null) return true;
        return false;
    }

    public boolean existSameDownStationId(Long lineId, Long downStationId) {
        if(sectionDao.getSectionByDownStationId(lineId, downStationId) != null) return true;
        return false;
    }

    public boolean canInsert(Section section) {

        /* line을 처음 생성하는 경우는 통과 */
        if(sectionDao.findAllByLineId(section.getLineId()).size() == 0) {
            return true;
        }

        // todo : alreadExistInLine 메서드 손봐야 할듯
        boolean upStationExist = sectionDao.alreadyExistInLine(section.getLineId(), section.getUpStationId());
        boolean downStationExist = sectionDao.alreadyExistInLine(section.getLineId(), section.getDownStationId());

        /* 둘다 등록되었거나, 둘다 등록되어 있지 않는 경우 */
        if(upStationExist == downStationExist) {
            return false;
        }

        return  true;
    }

    public List<Station> findSortedStationsByLineId(Long lineId) { //TODO : 뭔가 이 메서드 리팩토링이 필요하다.
        /* 주어진 Line 위에 정의된 모든 section들을 collect */
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        /* 현재 station에서 다음 station을 참조할 수 있는 map을 생성 */
        Map<Long, Long> upStationToDownStation = new HashMap<>();
        for(Section section : sections) {
            upStationToDownStation.put(section.getUpStationId(), section.getDownStationId());
        }

        /* 정렬된 station이 저장될 곳 */
        List<Station> stations = new ArrayList<>();

        /* 일렬로 탐색하면서 station을 수집한다. */
        Long currentId = getFirstStationId(lineId);
        while(currentId != null) {
            stations.add(stationService.findById(currentId));
            currentId = upStationToDownStation.get(currentId);
        }

        return stations;
    }

    private Long getFirstStationId(Long id) {
        List<Section> sections = sectionDao.findAllByLineId(id);

        Map<Long, Boolean> isStartPoint = new HashMap<>();

        /* 모든 station에 대해 start point = true 라는 의미로 초기화 */
        for(Section section : sections) {
            isStartPoint.put(section.getUpStationId(), true);
            isStartPoint.put(section.getDownStationId(), true);
        }

        /* 모든 section의 downStation Id에 대해 false 처리함 */
        for(Section section : sections) {
            isStartPoint.put(section.getDownStationId(), false);
        }

        /* downStation으로 등장하지 않은 한개의 노드(시작점) 을 return */
        return isStartPoint.keySet().stream()
                .filter(key -> isStartPoint.get(key))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("first station을 찾는데 실패했습니다."));
    }

    public void deleteStation(Long lineId, Long stationId) {
        deleteableCheck(lineId, stationId);

        Section forwardSection = sectionDao.getSectionByDownStationId(lineId, stationId);
        Section backwardSection = sectionDao.getSectionByUpStationId(lineId, stationId);

        /* 삭제할 station이 상행 종점인 경우 */
        if(null == forwardSection) {
            sectionDao.deleteById(backwardSection.getId());
            return;
        }

        /* 삭제한 station이 하행 종점인 경우 */
        if(null == backwardSection) {
            sectionDao.deleteById(forwardSection.getId());
            return;
        }

        /* 종점이 아닌 station을 삭제하는 경우 */
        sectionDao.deleteById(forwardSection.getId());
        sectionDao.deleteById(backwardSection.getId());

        Section newSection = new Section(
                forwardSection.getUpStationId(),
                backwardSection.getDownStationId(),
                lineId,
                forwardSection.getDistance() + backwardSection.getDistance()
        );

        sectionDao.save(newSection);
    }

    private void deleteableCheck(Long lineId, Long stationId) {
        if(!sectionDao.alreadyExistInLine(lineId, stationId)) {
            throw new NotFoundException("삭제할 station이 존재하지 않습니다.");
        }

        /* 존재하는 section의 수가 1 이하일 경우 */
        if(sectionDao.findAllByLineId(lineId).size() <= 1) {
            throw new CannotConstructRightSectionsForLine(sectionDao.findAllByLineId(lineId).size() + " " + "해당 line에서 더 이상 station을 삭제할 수 없습니다.");
        }
    }
}
