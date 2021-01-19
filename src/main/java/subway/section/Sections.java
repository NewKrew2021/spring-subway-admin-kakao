package subway.section;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        if (!sorted(sections)) {
            throw new IllegalArgumentException("Sections should be sorted by distance order");
        }

        if (hasDuplicates(sections)) {
            throw new IllegalArgumentException("Sections cannot have duplicate elements");
        }

        this.sections = Collections.unmodifiableList(sections);
    }

    public List<Long> getStationIDs() {
        return sections.stream()
                .map(Section::getStationID)
                .collect(Collectors.toList());
    }

    public Section insert(Section upSectionParameters, Section downSectionParameters) {
        if (!areValidSections(upSectionParameters, downSectionParameters)) {
            return null;
        }

        final int NOT_DEFINED = Integer.MAX_VALUE;
        int distanceDiff = downSectionParameters.distanceDiff(upSectionParameters);

        Section existingSection = findSection(upSectionParameters);
        Section newSectionParameters = downSectionParameters;
        int newSectionDistance = NOT_DEFINED;
        if (!sectionExists(existingSection)) {
            existingSection = findSection(downSectionParameters);
            newSectionParameters = upSectionParameters;
            newSectionDistance = existingSection.getDistance() - distanceDiff;
        }

        if (newSectionDistance == NOT_DEFINED) {
            newSectionDistance = existingSection.getDistance() + distanceDiff;
        }

        Section newSection = new Section(newSectionParameters.getLineID(),
                newSectionParameters.getStationID(),
                newSectionDistance);

        if (!haveValidDistance(existingSection, newSection)) {
            return null;
        }

        return newSection;
    }

    public boolean hasMinimumSectionCount() {
        return sections.size() <= 2;
    }

    public boolean hasNoSections() {
        return sections.isEmpty();
    }

    private Section findSection(Section section) {
        return sections.stream()
                .filter(sec -> sec.equals(section))
                .findFirst()
                .orElse(null);
    }

    private boolean areValidSections(Section upSection, Section downSection) {
        return sectionExists(upSection) != sectionExists(downSection);
    }

    private boolean sectionExists(Section sectionParameters) {
        return findSection(sectionParameters) != null;
    }

    private boolean haveValidDistance(Section existingSection, Section newSection) {
        Section nextSection = getNextSection(existingSection, newSection);
        if (isHighestOrLowestSection(nextSection)) {
            return true;
        }

        return existingSection.isCloserFromThan(newSection, nextSection);
    }

    private Section getNextSection(Section existingSection, Section newSection) {
        try {
            return sections.get(getNextSectionIdx(existingSection, newSection));
        } catch (IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    private int getNextSectionIdx(Section existingSection, Section newSection) {
        if (existingSection.isUpperThan(newSection)) {
            return sections.indexOf(existingSection) + 1;
        }

        return sections.indexOf(existingSection) - 1;
    }

    private boolean isHighestOrLowestSection(Section nextSection) {
        return nextSection == null;
    }

    private boolean sorted(List<Section> sections) {
        return sections.stream()
                .sorted(Comparator.comparingInt(Section::getDistance))
                .collect(Collectors.toList())
                .equals(sections);
    }

    private boolean hasDuplicates(List<Section> sections) {
        return sections.stream()
                .distinct()
                .count() != sections.size();
    }
}
