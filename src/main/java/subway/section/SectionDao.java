package subway.section;

import org.springframework.util.ReflectionUtils;
import subway.DuplicateException;
import subway.NotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SectionDao {
    private Long seq = 0L;
    private List<Section> sections = new ArrayList<>();
    private static SectionDao instance = new SectionDao();

    private SectionDao() {

    }

    public static SectionDao getInstance() {
        return instance;
    }

    public Section save(Section section){
        if(!canInsert(section)){
           throw new RuntimeException();
        }

        if(existSameUpstationId(section.getLineId(), section.getUpStationId())) {
            Section oldSection = getSectionByUpStationId(section.getLineId(), section.getUpStationId());
            deleteById(oldSection.getId());
            addNewBackwardSection(section, oldSection);
        }

        if(existSameDownStationId(section.getLineId(), section.getDownStationId())) {
            Section oldSection = getSectionByDownStationId(section.getLineId(), section.getDownStationId());
            deleteById(oldSection.getId());
            addNewForwardSection(section, oldSection);
        }

        return insertAtDB(section);
    }

    private void addNewBackwardSection(Section section, Section oldSection){
        Section newSection = new Section(
                section.getDownStationId(),
                oldSection.getDownStationId(),
                section.getLineId(),
                oldSection.getDistance() - section.getDistance()
        );

        insertAtDB(newSection);
    }

    private void addNewForwardSection(Section section, Section oldSection) {
        Section newSection = new Section(
                oldSection.getUpStationId(),
                section.getUpStationId(),
                section.getLineId(),
                oldSection.getDistance() - section.getDistance()
        );

        insertAtDB(newSection);
    }

    private Section insertAtDB(Section section) {
        //System.out.println("HO insertAtDB");
        Section persistSection = createNewObject(section);
        sections.add(persistSection);

        return persistSection;
    }

    public boolean existSameUpstationId(Long lineId, Long upStationId) {
        if(getSectionByUpStationId(lineId, upStationId) != null) return true;
        return false;
    }

    public boolean existSameDownStationId(Long lineId, Long downStationId) {
        if(getSectionByDownStationId(lineId, downStationId) != null) return true;
        return false;
    }

    public void deleteById(Long id) {
        sections.removeIf(it -> it.getId().equals(id));
    }

    public List<Section> findAllByLineId(Long id){
        return sections.stream()
                .filter(section -> section.getLineId().equals(id))
                .collect(Collectors.toList());
    }

    private Long getFirstStationId(Long id) {
        List<Section> sections = findAllByLineId(id);

        Map<Long, Boolean> isStartPoint = new HashMap<>();

        /* 모든 station에 대해 start point = true 라는 의미로 초기화 */
        for(Section section : sections){
            isStartPoint.put(section.getUpStationId(), true);
            isStartPoint.put(section.getDownStationId(), true);
        }

        /* 모든 section의 downStation Id에 대해 false 처리함 */
        for(Section section : sections){
            isStartPoint.put(section.getDownStationId(), false);
        }

        /* downStation으로 등장하지 않은 한개의 노드(시작점) 을 return */
        return isStartPoint.keySet().stream()
                .filter(key -> isStartPoint.get(key))
                .findFirst()
                .orElseThrow(() -> new NotFoundException());
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, upstationId가 일치하는 section을 return
     */
    private Section getSectionByUpStationId(Long lineId, Long upStationId){
        List<Section> sectionsInLine = findAllByLineId(lineId);

        return sectionsInLine.stream()
                .filter(it -> it.getUpStationId().equals(upStationId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 한 라인에 존재하는 모든 section들 중에서, downStationId가 일치하는 section을 return
     */
    private Section getSectionByDownStationId(Long lineId, Long downStationId){
        List<Section> sectionsInLine = findAllByLineId(lineId);

        return sectionsInLine.stream()
                .filter(it -> it.getDownStationId().equals(downStationId))
                .findFirst()
                .orElse(null);
    }

    public List<Long> findSortedIdsByLineId(Long lineId){
        /* 주어진 Line 위에 정의된 모든 section들을 collect */
        List<Section> sections = findAllByLineId(lineId);

        /* 현재 station에서 다음 station을 참조할 수 있는 map을 생성 */
        Map<Long, Long> upStationToDownStation = new HashMap<>();
        for(Section section : sections){
            upStationToDownStation.put(section.getUpStationId(), section.getDownStationId());
        }

        /* 정렬된 station id가 저장될 곳 */
        List<Long> stationIds = new ArrayList<>();

        /* 일렬로 탐색하면서 station id를 수집한다. */
        Long currentId = getFirstStationId(lineId);
        while(currentId != null) {
            stationIds.add(currentId);
            currentId = upStationToDownStation.get(currentId);
        }

        return stationIds;
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    public boolean canInsert(Section section){
        if(findAllByLineId(section.getLineId()).size() == 0) {
            return true;
        }

        boolean upStationExist = alreadyExistInLine(section.getLineId(), section.getUpStationId());
        boolean downStationExist = alreadyExistInLine(section.getLineId(), section.getDownStationId());

        /* 둘다 등록되었거나, 둘다 등록되어 있지 않는 경우 */
        if(upStationExist == downStationExist){
            return false;
        }

        return  true;
    }

    private boolean alreadyExistInLine(Long lineId, Long stationId){
        List<Section> sectionsInLine = findAllByLineId(lineId);
        return sectionsInLine.stream()
                .anyMatch(it -> (it.getUpStationId().equals(stationId) || (it.getDownStationId().equals(stationId))));
    }

    public void deleteStation(Long lineId, Long stationId) {
        Section forwardSection = getSectionByDownStationId(lineId, stationId);
        Section backwardSection = getSectionByUpStationId(lineId, stationId);

        if(forwardSection == null || backwardSection == null) {
            throw new RuntimeException();
        }

        deleteById(forwardSection.getId());
        deleteById(backwardSection.getId());

        Section newSection = new Section(
                forwardSection.getUpStationId(),
                backwardSection.getDownStationId(),
                lineId,
                forwardSection.getDistance() + backwardSection.getDistance()
        );

        insertAtDB(newSection);
    }
}
