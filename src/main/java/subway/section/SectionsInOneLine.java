package subway.section;

import subway.exceptions.CannotConstructRightSectionsForLine;

import java.util.ArrayList;
import java.util.List;

public class SectionsInOneLine {
    List<Section> sections;
    List<Long> stations;

    private void createStationList(){
        stations = new ArrayList<>();
        for(Section section : sections) {
            stations.add(section.getUpStationId());
            stations.add(section.getDownStationId());
        }
    }

    public SectionsInOneLine(List<Section> sections) {
        this.sections = sections;
        createStationList();
    }

    public void validateSave(Section sectionToSave) {
        /* line을 처음 생성하는 경우는 통과 */
        if(sections.size() == 0) return;

        boolean upStationExist = stations.contains(sectionToSave.getUpStationId());
        boolean downStationExist = stations.contains(sectionToSave.getDownStationId());

        /* 둘다 등록되었거나, 둘다 등록되어 있지 않는 경우 */
        if(upStationExist == downStationExist) {
            throw new CannotConstructRightSectionsForLine("잘못된 section 저장입니다.");
        }
    }

    public Section getSectionToBeUpdated(Section newSection) {
        Section upIdSameSection = findSectionThatHasSameUpStationAs(newSection.getUpStationId());
        Section downIdSameSection = findSectionThatHasSameDownStationAs(newSection.getDownStationId());

        if(null != upIdSameSection && null != downIdSameSection) {
            throw new CannotConstructRightSectionsForLine("업데이트 될 section을 찾기 전에, section이 저장될 수 있는지 먼저 확인되어야 합니다.");
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
}
