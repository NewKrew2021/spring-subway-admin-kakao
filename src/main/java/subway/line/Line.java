package subway.line;

import subway.station.Station;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<Section> sections;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, int extraFare, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
        this.sections = new LinkedList<>();
        this.sections.addAll(sections);
    }

    public LineResponse toDto() {
        return new LineResponse(id, name, color, extraFare, null);
    }

    public boolean insertSection(Station upStation, Station downStation, int distance) {
        ListIterator<Section> upSectionIt = findSectionIterator(upStation);
        ListIterator<Section> downSectionIt = findSectionIterator(downStation);

        if ((upSectionIt == null && downSectionIt == null) || (upSectionIt != null && downSectionIt != null)) {
            return false;
        }

        Section newSection;
        if (upSectionIt == null) {
            newSection = new Section(upStation, distance);
            if (!downSectionIt.hasPrevious()) {
                sections.add(0, newSection);
                return true;
            }

            downSectionIt.previous();
            upSectionIt = downSectionIt;
        } else {
            newSection = new Section(downStation, 0);
        }

        Section current = upSectionIt.next();
        if (upSectionIt.hasNext() && current.getDistance() <= distance) {
            return false;
        }
        newSection.setDistance(Math.max(current.getDistance() - distance, 0));
        current.setDistance(distance);
        upSectionIt.add(newSection);

        return true;
    }

    public boolean deleteById(Long id) {
        if (sections.size() <= 2) {
            return false;
        }

        Section section = sections.stream()
                .filter(sec -> sec.getStation().getId().equals(id))
                .findFirst()
                .orElse(null);

        if (section == null) {
            return false;
        }

        return sections.removeIf(sec -> sec.equals(section));
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

    public int getExtraFare() {
        return extraFare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, extraFare, sections);
    }

    private ListIterator<Section> findSectionIterator(Station upStation) {
        Section temp;
        ListIterator<Section> upSectionIt = null;
        for (ListIterator<Section> it = sections.listIterator(); it.hasNext(); ) {
            temp = it.next();
            if (temp.getStation().equals(upStation)) {
                upSectionIt = sections.listIterator(it.previousIndex());
            }
        }

        return upSectionIt;
    }
}
