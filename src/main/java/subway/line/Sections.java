package subway.line;

import subway.exception.NoContentException;
import subway.exception.TwoStationException;
import subway.station.Station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Sections {
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public void initSections(List<Section> sections) {
        Map<Station, List<Section>> countMap = new HashMap<>();
        sections.forEach(section -> {
            countMap.computeIfAbsent(section.getUpStation(), (key)->new ArrayList<>());
            countMap.get(section.getUpStation()).add(section);
            countMap.computeIfAbsent(section.getDownStation(),(key)->new ArrayList<>());
            countMap.get(section.getDownStation()).add(section);
        });
        Section firstSection = getFirstSection(countMap);
        Section lastSection = getLastSection(countMap);
        fillSections(countMap, lastSection, firstSection);
    }

    private void fillSections(Map<Station, List<Section>> countMap, Section lastSection, Section present) {
        this.sections.add(present);
        while (present.getDownStation() != lastSection.getUpStation()) {
            Section finalPresent = present;
            present = countMap.get(present.getDownStation()).stream()
                    .filter(section -> finalPresent.getDownStation().getId().equals(section.getUpStation().getId()))
                    .findAny()
                    .orElseThrow(() -> {
                        throw new NoContentException("섹션이 도중에 없습니다.");
                    });
            this.sections.add(present);
        }
        this.sections.add(lastSection);
    }

    private Section getLastSection(Map<Station, List<Section>> countMap) {
        List<Section> ret = new ArrayList<>();
        countMap.forEach((station, sections) -> {
            if (sections.size() == 1 &&
                    sections.stream()
                            .anyMatch(section -> section.getDownStation() == station)) {
                ret.add(new Section(station, null, Integer.MAX_VALUE / 2));
            }
        });
        return ret.get(0);
    }

    private Section getFirstSection(Map<Station, List<Section>> countMap) {
        List<Section> ret = new ArrayList<>();
        countMap.forEach((station, sections) -> {
            if (sections.size() == 1 &&
                    sections.stream()
                            .anyMatch(section -> section.getUpStation() == station)) {
                ret.add(new Section(null, station, Integer.MAX_VALUE / 2));
            }
        });
        return ret.get(0);
    }

    public void deleteStation(Long stationId) {
        if (sections.size() <= 3) {
            throw new TwoStationException();
        }
        int sectionIndex = IntStream.range(1, sections.size())
                .filter(i -> sections.get(i).getUpStation().getId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new NoContentException("삭제 하려는데 데이터가 없습니다."));
        sections.set(sectionIndex, new Section(sections.get(sectionIndex - 1).getUpStation(),
                sections.get(sectionIndex).getDownStation(),
                sections.get(sectionIndex - 1).getDistance() + sections.get(sectionIndex).getDistance()));
        sections.remove(sectionIndex - 1);
    }

    public int size() {
        return sections.size();
    }

    public Section get(int index) {
        return sections.get(index);
    }

    public void add(Section section) {
        sections.add(section);
    }

    public void set(int sectionIndex, Section section) {
        sections.set(sectionIndex, section);
    }

    public void remove(int index) {
        sections.remove(index);
    }
}
