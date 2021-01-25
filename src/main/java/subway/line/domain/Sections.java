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
