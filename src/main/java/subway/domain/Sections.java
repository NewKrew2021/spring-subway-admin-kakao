package subway.domain;

import subway.exception.AlreadyExistDataException;
import subway.exception.DataEmptyException;
import subway.exception.DeleteImpossibleException;

import java.util.*;

public class Sections {
    public static final int ZERO = 0;
    List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        Station cur = getStartStation(sections);
        Station dest = getEndStation(sections);
        List<Section> orderedSections = new LinkedList<>();
        while (!cur.equals(dest)) {
            Station finalCur = cur;
            Section section = sections.stream()
                    .filter(sec -> sec.getUpStation().equals(finalCur))
                    .findFirst()
                    .get();
            orderedSections.add(section);
            cur = section.getDownStation();
        }
        this.sections = orderedSections;
    }

    public List<Station> getStations() {
        Set<Station> resultSet = new LinkedHashSet<>();
        for (Section section : sections) {
            resultSet.add(section.getUpStation());
            resultSet.add(section.getDownStation());
        }
        return new ArrayList<>(resultSet);
    }

    private Station getStartStation(List<Section> sections) {
        return sections.stream()
                .filter(section -> isInSectionUpStation(sections, section))
                .findFirst()
                .orElseThrow(DataEmptyException::new)
                .getUpStation();
    }

    private boolean isInSectionUpStation(List<Section> sections, Section section) {
        return sections.stream()
                .noneMatch(section1 -> section.getUpStation().equals(section1.getDownStation()));
    }

    private Station getEndStation(List<Section> sections) {
        return sections.stream()
                .filter(section -> isInSectionDownStation(sections, section))
                .findFirst()
                .orElseThrow(DataEmptyException::new)
                .getDownStation();
    }

    private boolean isInSectionDownStation(List<Section> sections, Section section) {
        return sections.stream()
                .noneMatch(section1 -> section.getDownStation().equals(section1.getUpStation()));
    }

    public Station getStartStation() {
        return this.sections
                .get(ZERO)
                .getUpStation();
    }

    public Station getEndStation() {
        return this.sections
                .get(this.sections.size() - 1)
                .getDownStation();
    }

    public Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(sec -> sec.getUpStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(sec -> sec.getDownStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    public boolean existSectionByStation(Station station) {
        return sections.stream()
                .anyMatch(sec -> sec.getUpStation().equals(station) || sec.getDownStation().equals(station));
    }

    public boolean isCanSaveSection(Section section) {
        return sections.stream()
                .anyMatch(sec -> sec.getUpStation().equals(section.getUpStation())
                        || sec.getDownStation().equals(section.getUpStation())
                        || sec.getUpStation().equals(section.getDownStation())
                        || sec.getDownStation().equals(section.getDownStation()));
    }

    public boolean isPossibleToDelete() {
        return sections.size() > 1;
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isExistUpStationAndMiddleSection(Section section) {
        return findSectionByUpStation(section.getUpStation()) != null && !getEndStation().equals(section.getDownStation());
    }

    public boolean isExistDownStationAndMiddleSection(Section section) {
        return findSectionByDownStation(section.getDownStation()) != null && !getStartStation().equals(section.getDownStation());
    }

    public Long getLineId() {
        return sections.get(ZERO).getLineId();
    }

    public void addSection(Section section) {
        if (existSection(section)) {
            throw new AlreadyExistDataException();
        }
        if (sections.size() > ZERO && !isCanSaveSection(section)) {
            throw new IllegalStationException();
        }
        if (isExistUpStationAndMiddleSection(section)) {
            addSectionBack(section);
        }
        if (isExistDownStationAndMiddleSection(section)) {
            addSectionFront(section);
        }
        sections.add(section);
    }

    private boolean existSection(Section section) {
        return existSectionByStation(section.getUpStation()) && existSectionByStation(section.getDownStation());
    }

    private void addSectionBack(Section section) {
        Section nextSection = findSectionByUpStation(section.getUpStation());
        sections.remove(nextSection);
        sections.add(new Section(section.getDownStation(), nextSection.getDownStation(), nextSection.getDistance() - section.getDistance(), section.getLineId()));
    }

    private void addSectionFront(Section section) {
        Section prevSection = findSectionByDownStation(section.getDownStation());
        sections.remove(prevSection);
        Section newSection = new Section(prevSection.getUpStation(), section.getUpStation(), prevSection.getDistance() - section.getDistance(), section.getLineId());
        sections.add(newSection);
    }

    public void deleteSection(Station station) {
        deleteStationValidate(station);
        delete(station);
    }

    private void deleteStationValidate(Station station) {
        if (!isPossibleToDelete() || !existSectionByStation(station)) {
            throw new DeleteImpossibleException();
        }
    }

    private void delete(Station station) {
        Section nextSection = findSectionByUpStation(station);
        Section prevSection = findSectionByDownStation(station);
        Long lineId = getLineId();
        if (prevSection != null) {
            sections.remove(prevSection);
        }
        if (nextSection != null) {
            sections.remove(nextSection);
        }
        if (nextSection != null && prevSection != null) {
            sections.add(new Section(prevSection.getUpStation(), nextSection.getDownStation(),
                    prevSection.getDistance() + nextSection.getDistance(), lineId));
        }
    }
}
