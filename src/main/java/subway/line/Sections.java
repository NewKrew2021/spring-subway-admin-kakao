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

    public List<Long> getStationIDs() {
        return sections.stream()
                .map(Section::getStationID)
                .collect(Collectors.toList());
    }

    public List<Integer> getDistances() {
        return sections.stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
    }

    public Section insert(Section upSection, Section downSection) {
        Long lineID = upSection.getLineID();
        Long upStationID = upSection.getStationID();
        Long downStationID = downSection.getStationID();

        int distance = downSection.getDistance();
        if (!validateStations(upStationID, downStationID)) {
            return null;
        }

        Section existingSection = findExistingSection(lineID, upStationID, downStationID);
        Section newSection = makeNewSection(lineID, upStationID, downStationID, distance);
        if (!validateDistance(existingSection.getDistance(), newSection.getDistance())) {
            return null;
        }

        return newSection;
    }

    private Section findByStationID(Long stationID) {
        return sections.stream()
                .filter(section -> section.getStationID() == stationID)
                .findFirst()
                .orElse(null);
    }

    private Section makeNewSection(Long lineID, Long upStationID, Long downStationID, int distance) {
        Section upSection = findByStationID(upStationID);
        Section downSection = findByStationID(downStationID);

        if (isExisting(upSection)) {
            return new Section(lineID, downStationID, upSection.getDistance() + distance);
        }

        return new Section(lineID, upStationID, downSection.getDistance() - distance);
    }

    private Section findExistingSection(Long lineID, Long upStationID, Long downStationID) {
        Section upSection = findByStationID(upStationID);
        Section downSection = findByStationID(downStationID);

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

    private boolean validateStations(Long upStationID, Long downStationID) {
        return isExisting(findByStationID(upStationID)) != isExisting(findByStationID(downStationID));
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
