package subway.domain;

import subway.exception.AlreadyExistDataException;
import subway.exception.DataEmptyException;
import subway.exception.DeleteImpossibleException;
import subway.exception.IllegalStationException;

import java.util.*;

public class Sections {
    public static final int ZERO = 0;
    List<Section> sections;
    List<Station> stations;

    public Sections(List<Section> sections) {
        Long cur = getStartStationId(sections);
        Long dest = getEndStationId(sections);
        List<Section> orderedSections = new LinkedList<>();
        while (!cur.equals(dest)) {
            Long finalCur = cur;
            Section section = sections.stream()
                    .filter(sec -> sec.getUpStationId().equals(finalCur))
                    .findFirst()
                    .get();
            orderedSections.add(section);
            cur = section.getDownStationId();
        }
        this.sections = orderedSections;
    }

    public Sections(List<Section> sections, List<Station> stations) {
        this(sections);
        this.stations = stations;
    }

    private Long getStartStationId(List<Section> sections) {
        return sections.stream()
                .filter(section -> isInSectionUpStation(sections, section))
                .findFirst()
                .orElseThrow(DataEmptyException::new)
                .getUpStationId();
    }

    private boolean isInSectionUpStation(List<Section> sections, Section section) {
        return sections.stream()
                .noneMatch(section1 -> section.getUpStationId().equals(section1.getDownStationId()));
    }

    private Long getEndStationId(List<Section> sections) {
        return sections.stream()
                .filter(section -> isInSectionDownStation(sections, section))
                .findFirst()
                .orElseThrow(DataEmptyException::new)
                .getDownStationId();
    }

    private boolean isInSectionDownStation(List<Section> sections, Section section) {
        return sections.stream()
                .noneMatch(section1 -> section.getDownStationId().equals(section1.getUpStationId()));
    }

    public Long getStartStation() {
        return this.sections
                .get(ZERO)
                .getUpStationId();
    }

    public Long getEndStation() {
        return this.sections
                .get(this.sections.size() - 1)
                .getDownStationId();
    }

    public Section findSectionByUpStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getUpStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionByDownStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getDownStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionByStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getUpStationId().equals(id) || sec.getDownStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean isCanSaveSection(Section section) {
        return sections.stream()
                .anyMatch(sec -> sec.getUpStationId().equals(section.getUpStationId())
                        || sec.getDownStationId().equals(section.getUpStationId())
                        || sec.getUpStationId().equals(section.getDownStationId())
                        || sec.getDownStationId().equals(section.getDownStationId()));
    }

    public boolean isPossibleToDelete() {
        return sections.size() > 1;
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isExistUpStationAndMiddleSection(Section section) {
        return findSectionByUpStationId(section.getUpStationId()) != null && !getEndStation().equals(section.getDownStationId());
    }

    public boolean isExistDownStationAndMiddleSection(Section section) {
        return findSectionByDownStationId(section.getDownStationId()) != null && !getStartStation().equals(section.getDownStationId());
    }

    public Long getLineId() {
        return sections.get(ZERO).getLineId();
    }

    public List<Station> getStations() {
        return stations;
    }

    public void addSection(Section section) {
        if (existSection(section)) {
            throw new AlreadyExistDataException();
        }
        if (!isCanSaveSection(section)) {
            throw new IllegalStationException();
        }
        if (isExistUpStationAndMiddleSection(section)) {
            addSectionBack(section);
        }
        if (isExistDownStationAndMiddleSection(section)) {
            addSectionFront(section);
        }
        sections.add(section);
        System.out.println(sections);
    }

    private boolean existSection(Section section) {
        return sections.stream()
                .anyMatch(section1 -> section.getUpStationId().equals(section1.getUpStationId()) && section.getDownStationId().equals(section1.getDownStationId()));
    }

    private void addSectionBack(Section section) {
        Section nextSection = findSectionByUpStationId(section.getUpStationId());
        sections.remove(nextSection);
        sections.add(new Section(section.getDownStationId(), nextSection.getDownStationId(), nextSection.getDistance() - section.getDistance(), section.getLineId()));
    }

    private void addSectionFront(Section section) {
        Section prevSection = findSectionByDownStationId(section.getDownStationId());
        sections.remove(prevSection);
        Section newSection = new Section(prevSection.getUpStationId(), section.getUpStationId(), prevSection.getDistance() - section.getDistance(), section.getLineId());
        sections.add(newSection);
    }

    public void deleteStation(Long stationId) {
        if (!isPossibleToDelete() || findSectionByStationId(stationId) == null) {
            throw new DeleteImpossibleException();
        }
        delete(stationId);
    }

    private void delete(Long stationId) {
        Section nextSection = findSectionByUpStationId(stationId);
        Section prevSection = findSectionByDownStationId(stationId);
        if (prevSection != null) {
            sections.remove(prevSection);
        }
        if (nextSection != null) {
            sections.remove(nextSection);
        }
        if (nextSection != null && prevSection != null) {
            sections.add(new Section(prevSection.getUpStationId(), nextSection.getDownStationId(),
                    prevSection.getDistance() + nextSection.getDistance(), getLineId()));
        }
    }
}
