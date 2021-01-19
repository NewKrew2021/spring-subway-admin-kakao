package subway.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sections {

    private static final int MIN_SECTION_SIZE = 1;
    private List<Section> sectionList;

    public Sections(List<Section> sectionList){
        this.sectionList = sectionList;
    }

    public boolean isPossibleLengthToDelete() {
        return sectionList.size() > MIN_SECTION_SIZE;
    }

    public Section getSectionByDownStationId(Long downStationId){
        return sectionList
                .stream()
                .filter(section-> section.getDownStationId().equals(downStationId))
                .findFirst().orElse(null);
    }

    public Section getSectionByUpStationId(Long upStationId){
        return sectionList
                .stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst().orElse(null);
    }

    public Section getModifiedSection(Section newSection) {
        for (Section oldSection : sectionList) {
            if (oldSection.canInsertMatchingUpStation(newSection)) {
                return new Section(oldSection.getId(), oldSection.getLineId(), newSection.getDownStationId(), oldSection.getDownStationId(), oldSection.getDistance() - newSection.getDistance());
            }
            if (oldSection.canInsertMatchingDownStation(newSection)) {
                return new Section(oldSection.getId(), oldSection.getLineId(), oldSection.getUpStationId(), newSection.getUpStationId(), oldSection.getDistance() - newSection.getDistance());
            }
        }
        return null;
    }

    public Map<Long, Long> getSectionMap(){
        Map<Long, Long> result = new HashMap<>();
        sectionList.stream().forEach(i->{
            result.put(i.getUpStationId(), i.getDownStationId());
        });
        return result;
    }
}
