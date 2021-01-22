package subway.section.domain;

import subway.exceptions.InvalidSectionException;

import java.util.List;

public class Sections {
    private static final String DUPLICATE_SECTION_EXCEPTION = "구간이 중복되었습니다.";
    private static final String NO_STATION_IN_LINE_EXCEPTION = "해당 노선에 포함되는 역이 존재하지 않습니다.";
    private static final String INVALID_DISTANCE_SECTION_EXCEPTION = "추가하려는 구간의 길이가 기존 구간의 길이보다 깁니다.";
    private static final String DELETE_SECTION_EXCEPTION = "구간이 1개밖에 없어 삭제하지 못합니다.";

    private List<Section> sections;

    public Sections(List<Section> sections){
        this.sections = sections;
    }

    public Section addSection(Long upStationId, Long downStationId, int distance){
        checkValidationSection(upStationId, downStationId);

        Section nowSection = new Section();
        if(anyMatchSection(upStationId)){
            Section upSection = findByStationId(upStationId);
            checkNextSectionDistanceValidation(upSection, distance);
            nowSection = new Section(upSection.getLineId(), downStationId, upSection.getDistance()+distance);
        }
        if(anyMatchSection(downStationId)){
            Section downSection = findByStationId(downStationId);
            checkPrevSectionDistanceValidation2(downSection, distance);
            nowSection = new Section(downSection.getLineId(), upStationId, downSection.getDistance()-distance);
        }

        return nowSection;
    }

    private void checkValidationSection(Long upStationId, Long downStationId) {
        if(anyMatchSection(upStationId) && anyMatchSection(downStationId)){
            throw new InvalidSectionException(DUPLICATE_SECTION_EXCEPTION);
        }
        if(NoMatchSection(upStationId, downStationId)){
            throw new InvalidSectionException(NO_STATION_IN_LINE_EXCEPTION);
        }
    }

    private void checkNextSectionDistanceValidation(Section upSection, int distance){
        int NextSectionIndex = sections.indexOf(upSection)+1;
        if(NextSectionIndex < sections.size() && sections.get(NextSectionIndex).getDistance() <= distance){
            throw new InvalidSectionException(INVALID_DISTANCE_SECTION_EXCEPTION);
        }
    }
    private void checkPrevSectionDistanceValidation2(Section downSection, int distance){
        int PrevSectionIndex = sections.indexOf(downSection)-1;
        if(PrevSectionIndex >= 0 && (downSection.getDistance()-sections.get(PrevSectionIndex).getDistance()) <= distance){
            throw new InvalidSectionException(INVALID_DISTANCE_SECTION_EXCEPTION);
        }
    }

    private Section findByStationId(Long stationId){
        return sections.stream()
                .filter(section -> section.getStationId() == stationId)
                .findFirst()
                .orElse(new Section());
    }

    public boolean anyMatchSection(Long stationId){
        return sections.stream().anyMatch(section -> section.getStationId() == stationId);

    }

    public boolean NoMatchSection(Long upStationId, Long downStationId){
        return sections.size() > 0 && sections.stream().allMatch(section ->
                    section.getStationId() != upStationId &&
                    section.getStationId() != downStationId);
    }

    public void checkDeleteValidationSection(){
        if (sections.size() < 3){
            throw new InvalidSectionException(DELETE_SECTION_EXCEPTION);
        }
    }
}
