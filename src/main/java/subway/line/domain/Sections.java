package subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {
    private static final String SECTION_NOT_VALID_DELETE_MESSAGE = "유일한 세션은 지울 수 없습니다.";
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        section.checkValidInsert(sections);

        Map<Long, Long> upStationAndDownStation = getUpStationAndDownStation();

        sections.add(section);
        if (SectionStatus.getSectionStatus(upStationAndDownStation, section) == SectionStatus.UP_STATION_MATCHING) {
            updateWhenUpStationMatching(section);
            return;
        }
        if (SectionStatus.getSectionStatus(upStationAndDownStation, section) == SectionStatus.DOWN_STATION_MATCHING) {
            updateWhenDownStationMatching(section);
        }
    }

    public void deleteSection(Long lineId, Long stationId) {
        Section upStationMatch = getUpStationMatch(stationId);
        Section downStationMatch = getDownStationMatch(stationId);

        checkValidDelete(stationId);

        if (isMiddleStation(stationId)) {
            deleteWhenMiddleSection(lineId, upStationMatch, downStationMatch);
        }
        if (isTerminalStation(stationId)) {
            sections.remove((upStationMatch == null) ? downStationMatch : upStationMatch);
        }
    }

    private void deleteWhenMiddleSection(Long lineId, Section upStationMatch, Section downStationMatch) {
        int newDistance = upStationMatch.getDeleteNewDistance(downStationMatch);

        Section newSection = new Section(lineId, downStationMatch.getUpStationId(), upStationMatch.getDownStationId(), newDistance);
        sections.add(newSection);

        sections.remove(upStationMatch);
        sections.remove(downStationMatch);
    }

    private void updateWhenUpStationMatching(Section section) {
        Section upStationMatchSection = getUpStationMatch(section.getUpStationId());
        int newDistance = upStationMatchSection.getInsertNewDistance(section);
        sections.remove(upStationMatchSection);
        sections.add(new Section(upStationMatchSection.getId(), section.getLineId(), section.getDownStationId(), upStationMatchSection.getDownStationId(), newDistance));
    }

    private void updateWhenDownStationMatching(Section section) {
        Section downStationMatchSection = getDownStationMatch(section.getDownStationId());
        int newDistance = downStationMatchSection.getInsertNewDistance(section);
        sections.remove(downStationMatchSection);
        sections.add(new Section(downStationMatchSection.getId(), section.getLineId(), downStationMatchSection.getUpStationId(), section.getUpStationId(), newDistance));
    }

    public Section getUpStationMatch(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public Section getDownStationMatch(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public Map<Long, Long> getUpStationAndDownStation() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Section::getDownStationId));
    }

    public Map<Long, Integer> getDownStationAndDistance() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, Section::getDistance));
    }

    public List<Long> getStationIds() {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
    }

    public Long getLastSectionDownStationId() {
        return sections.get(sections.size() - 1).getDownStationId();
    }

    public void checkValidDelete(Long stationId) {
        if (sections.size() == 1 && (getUpStationMatch(stationId) != null || getDownStationMatch(stationId) != null)) {
            throw new SectionNotValidDeleteException(SECTION_NOT_VALID_DELETE_MESSAGE);
        }
    }

    public List<Section> sort(Long lineId, Map<Long, Long> upStationAndDownStation, Map<Long, Integer> downStationAndDistance) {
        Long startId = getUpStationAndDownStation().keySet().stream()
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

    public boolean isMiddleStation(Long stationId) {
        return getUpStationMatch(stationId) != null && getDownStationMatch(stationId) != null;
    }

    public boolean isTerminalStation(Long stationId) {
        return getUpStationMatch(stationId) != null ^ getDownStationMatch(stationId) != null;
    }
}