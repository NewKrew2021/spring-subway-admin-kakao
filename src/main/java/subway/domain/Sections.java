package subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Sections {

    private static final int MIN_SECTION_SIZE = 1;
    private List<Section> sectionList;

    public Sections(List<Section> sectionList){
        this.sectionList = sectionList;
    }

    public boolean isPossibleLengthToDelete() {
        return sectionList.size() > MIN_SECTION_SIZE;
    }

    public Optional<Section> getSectionByDownStationId(Long downStationId){
        return sectionList
                .stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst();
    }

    public Optional<Section> getSectionByUpStationId(Long upStationId){
        return sectionList
                .stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst();
    }

    public Optional<Section> getModifiedSection(Section newSection) {
        for (Section oldSection : sectionList) {
            if (oldSection.canInsertMatchingUpStation(newSection)) {
                oldSection.modifyUpByInsertingSection(newSection);
                return Optional.of(oldSection);
            }
            if (oldSection.canInsertMatchingDownStation(newSection)) {
                oldSection.modifyDownByInsertingSection(newSection);
                return Optional.of(oldSection);
            }
        }
        return Optional.empty();
    }

    public Map<Long, Long> getSectionMap(){
        Map<Long, Long> result = new HashMap<>();
        for (Section i : sectionList) {
            result.put(i.getUpStationId(), i.getDownStationId());
        }
        return result;
    }
}
