package subway.domain.section;

import subway.exceptions.IllegalSectionSave;
import subway.exceptions.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class SectionsInOneLine {
    List<Section> sections;

    public List<Long> getStationList() {
        Set<Long> stationIds = new HashSet<>();
        for(Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return new ArrayList<>(stationIds);
    }

    public SectionsInOneLine(List<Section> sections) {
        this.sections = sections;
    }

    public void validateSave(Section sectionToSave) {
        List<Long> stationIds = getStationList();
        /* line을 처음 생성하는 경우는 통과 */
        if(sections.size() == 0) return;

        boolean upStationExist = stationIds.contains(sectionToSave.getUpStationId());
        boolean downStationExist = stationIds.contains(sectionToSave.getDownStationId());

        /* 둘다 등록되었거나, 둘다 등록되어 있지 않는 경우 */
        if(upStationExist == downStationExist) {
            throw new IllegalSectionSave("저장할 section이 유효하지 않습니다.");
        }
    }

    public Section getSectionToBeUpdated(Section newSection) {
        Section upIdSameSection = findSectionThatHasSameUpStationAs(newSection.getUpStationId());
        Section downIdSameSection = findSectionThatHasSameDownStationAs(newSection.getDownStationId());

        if(null != upIdSameSection && null != downIdSameSection) {
            throw new IllegalSectionSave("업데이트 될 section을 찾기 전에, section이 저장될 수 있는지 먼저 확인되어야 합니다.");
        }
        if(null != upIdSameSection) {
            return upIdSameSection.subtractBasedOnUpStation(newSection);
        }
        if(null != downIdSameSection) {
            return downIdSameSection.subtractBasedOnDownStation(newSection);
        }
        /* update 되어야 할 section이 존재하지 않다. */
        return null;
    }

    private Section findSectionThatHasSameUpStationAs(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst().orElse(null);
    }

    private Section findSectionThatHasSameDownStationAs(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst().orElse(null);
    }

    private Long getFirstStationId() {
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

    public List<Long> getSortedStations() {
        /* 현재 station에서 다음 station을 참조할 수 있는 map을 생성 */
        Map<Long, Long> upStationToDownStation = new HashMap<>();
        for(Section section : sections) {
            upStationToDownStation.put(section.getUpStationId(), section.getDownStationId());
        }

        /* 정렬된 station이 저장될 곳 */
        List<Long> stationIds = new ArrayList<>();

        /* 일렬로 탐색하면서 station을 수집한다. */
        Long currentId = getFirstStationId();
        while(currentId != null) {
            stationIds.add(currentId);
            currentId = upStationToDownStation.get(currentId);
        }

        return stationIds;
    }

    public List<Section> getSectionsThatContain(Long stationId) {
        return sections.stream().filter(section ->
                stationId.equals(section.getUpStationId()) || stationId.equals(section.getDownStationId())
        ).collect(Collectors.toList());
    }
}
