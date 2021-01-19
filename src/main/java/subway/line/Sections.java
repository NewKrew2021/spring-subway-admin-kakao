package subway.line;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        if (!sorted(sections)) {
            throw new IllegalArgumentException("sections should be sorted by distance order");
        }

        this.sections = Collections.unmodifiableList(sections);
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

    public Section insert(Section upSection, Section downSection) {
        Long lineId = upSection.getLineId();
        Long upStationId = upSection.getStationId();
        Long downStationId = downSection.getStationId();

        int distance = downSection.getDistance();
        if (!validateStations(upStationId, downStationId)) {
            return null;
        }

        Section existingSection = findExistingSection(lineId, upStationId, downStationId);
        Section newSection = makeNewSection(lineId, upStationId, downStationId, distance);
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

    private Section makeNewSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Section upSection = findByStationId(upStationId);
        Section downSection = findByStationId(downStationId);

        if (isExisting(upSection)) {
            return new Section(lineId, downStationId, upSection.getDistance() + distance);
        }

        return new Section(lineId, upStationId, downSection.getDistance() - distance);
    }

    private Section findExistingSection(Long lineId, Long upStationId, Long downStationId) {
        Section upSection = findByStationId(upStationId);
        Section downSection = findByStationId(downStationId);

        return isExisting(upSection) ? upSection : downSection;
    }

    private boolean validateDistance(int existingDistance, int newDistance) {
        int diff = newDistance - existingDistance;

        for (Integer dist : getDistances()) {
            int tempDiff = dist - existingDistance;
            System.out.println(dist);
            if (diff * tempDiff > 0 && Math.abs(diff) >= Math.abs(tempDiff))
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

    private boolean sorted(List<Section> sections) {
        return sections.stream()
                .sorted(Comparator.comparingInt(Section::getDistance))
                .collect(Collectors.toList())
                .equals(sections);
    }
}
