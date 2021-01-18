package subway.line;

import subway.exception.NoContentException;
import subway.exception.TwoStationException;
import subway.station.Station;

import java.util.*;
import java.util.stream.IntStream;

public class Line {
    private Long id;
    private String name;
    private String color;
    private final List<Section> sections;

    public Line() {
        this.sections = new ArrayList<>();
    }

    public Line(String name, String color) {
        this();
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this(id, name, color);
        Map<Station, List<Section>> countMap = new HashMap<>();
        sections.forEach(section -> {
            countMap.computeIfAbsent(section.getUpStation(), (key)->new ArrayList<>());
            countMap.get(section.getUpStation()).add(section);
            countMap.computeIfAbsent(section.getDownStation(),(key)->new ArrayList<>());
            countMap.get(section.getDownStation()).add(section);
        });
        Section firstSection = getFirstSection(countMap);
        Section lastSection = getLastSection(countMap);
        Section present = firstSection;
        this.sections.add(present);
        while(true){
            Section finalPresent = present;
            if(finalPresent.getDownStation()==lastSection.getUpStation()){
                break;
            }
            present = countMap.get(present.getDownStation()).stream()
                    .filter(section -> {
                        return finalPresent.getDownStation().getId().equals(section.getUpStation().getId());})
                    .findAny()
                    .orElseThrow(()->{
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


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }


    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }

}
