package subway.section;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        sections.sort(Section::compareTo);
        this.sections = sections;
    }

    public List<Long> getStationIds() {
        return sections.stream()
                .map(Section::getStationId)
                .collect(Collectors.toList());
    }

    public List<Integer> getDistances() {
        return sections.stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
    }

    public Section insert(Long upStationId, Long downStationId, int distance) {
        if (!validateStations(upStationId, downStationId)) {
            return null;
        }

        Section existingSection = findExistingSection(upStationId, downStationId);
        Section newSection = makeNewSection(upStationId, downStationId, distance);
        if (!validateDistance(existingSection.getDistance(), newSection.getDistance())) {
            return null;
        }

        return newSection;
    }

    private Section findByStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getStationId() == stationId)
                .findFirst()
                .orElse(null);
    }

    private Section makeNewSection(Long upStationId, Long downStationId, int distance) {
        Section upSection = findByStationId(upStationId);
        Section downSection = findByStationId(downStationId);

        if (isExisting(upSection)) {
            return new Section(upSection.getLineId(), downStationId, upSection.getDistance() + distance);
        }

        return new Section(downSection.getLineId(), upStationId, downSection.getDistance() - distance);
    }

    private Section findExistingSection(Long upStationId, Long downStationId) {
        Section upSection = findByStationId(upStationId);
        Section downSection = findByStationId(downStationId);

        return isExisting(upSection) ? upSection : downSection;
    }

    private boolean validateDistance(int existingSectionDistance, int newSectionDistance) {
        int distanceGap = newSectionDistance - existingSectionDistance;

        for (Integer distance : getDistances()) {
            int tempGap = distance - existingSectionDistance;
            if (distanceGap * tempGap > 0 && Math.abs(distanceGap) >= Math.abs(tempGap))
                return false;
        }

        return true;
    }

    private boolean isExisting(Section section) {
        return section != null;
    }

    private boolean validateStations(Long upStationId, Long downStationId) {
        return isExisting(findByStationId(upStationId)) != isExisting(findByStationId(downStationId));
    }

    public boolean hasOnlyTwoSections() {
        return sections.size() <= 2;
    }
}
