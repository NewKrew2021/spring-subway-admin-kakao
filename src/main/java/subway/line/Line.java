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

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(name, color);
        sections.add(new Section(null, upStation, Integer.MAX_VALUE / 2));
        sections.add(new Section(upStation, downStation, distance));
        sections.add(new Section(downStation, null, Integer.MAX_VALUE / 2));
    }

    public Section addSection(Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            Section section = new Section(upStation, downStation, distance);
            sections.add(new Section(null, upStation, Integer.MAX_VALUE / 2));
            sections.add(section);
            sections.add(new Section(downStation, null, Integer.MAX_VALUE / 2));
            return section;
        }

        return makeAndInsertSection(upStation, downStation, distance);
    }

    private Section makeAndInsertSection(Station upStation, Station downStation, int distance) {
        int targetIndex = -1;

        int upIndex = IntStream.range(1, sections.size())
                .filter(i -> sections.get(i).getUpStation().getId().equals(upStation.getId()))
                .findAny()
                .orElse(-1);

        int downIndex = IntStream.range(0, sections.size() - 1)
                .filter(i -> sections.get(i).getDownStation().getId().equals(downStation.getId()))
                .findAny()
                .orElse(-1);

        if ((upIndex == -1) == (downIndex == -1)) {
            throw new NoContentException("둘 중 하나만 -1이여야함!");
        }

        if (distance >= sections.get(upIndex * downIndex * -1).getDistance()) {
            throw new NoContentException("길이가 맞지 않음");
        }

        if (upIndex != -1) {
            Section present = sections.get(upIndex);
            sections.set(upIndex, new Section(downStation, present.getDownStation(), present.getDistance() - distance));
            sections.add(upIndex, new Section(upStation, downStation, distance));
            targetIndex = upIndex;
        }

        if (downIndex != -1) {
            Section present = sections.get(downIndex);
            sections.set(downIndex, new Section(upStation, downStation, distance));
            sections.add(downIndex, new Section(present.getUpStation(), upStation, present.getDistance() - distance));
            targetIndex = downIndex + 1;
        }

        return sections.get(targetIndex);
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
