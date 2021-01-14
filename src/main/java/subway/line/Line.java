package subway.line;

import subway.station.Station;
import subway.station.StationDao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Line {
    private static final int FIRST_INDEX = 0;
    public static final int MIN_SECTION_SIZE = 1;
    private Long id;
    private String name;
    private String color;
    private LinkedList<Section> sections;

    public Line() {
    }

    public Line(String name, String color, LinkedList<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    public LinkedList<Section> getSections() {
        return sections;
    }

    public void addSection(Section newSection) {
        if (addFirstSection(newSection)) return;
        if (addLastSection(newSection)) return;

        checkSameStation(newSection);
        checkNoStation(newSection);

        if (addRightSection(newSection)) return;
        if (addLeftSection(newSection)) return;
    }

    private void checkNoStation(Section newSection) {
        if (sections.stream()
                .noneMatch(section -> section.getDownStationId() == newSection.getDownStationId() ||
                        section.getUpStationId() == newSection.getUpStationId())) {
            throw new IllegalArgumentException("두 역이 모두 등록되어 있지 않습니다.");
        }
    }

    private void checkSameStation(Section newSection) {
        if (sections.stream()
                .anyMatch(section -> section.getDownStationId() == newSection.getDownStationId() &&
                        section.getUpStationId() == newSection.getUpStationId())) {
            throw new IllegalArgumentException("이미 등록되어 있습니다.");
        }
    }

    private boolean addLeftSection(Section newSection) {
        Section originSection = sections.stream()
                .filter(section -> section.getDownStationId() == newSection.getDownStationId())
                .findFirst()
                .orElse(null);

        if (originSection != null) {
            validateDistance(newSection, originSection);
            int sectionIndex = sections.indexOf(originSection);
            Section modifiedSection = new Section(originSection.getUpStationId(), newSection.getUpStationId(),
                    originSection.getDistance() - newSection.getDistance());

            sections.set(sectionIndex, newSection);
            sections.add(sectionIndex, modifiedSection);
            return true;
        }
        return false;
    }

    private boolean addRightSection(Section newSection) {
        Section originSection = sections.stream()
                .filter(section -> section.getUpStationId() == newSection.getUpStationId())
                .findFirst()
                .orElse(null);

        if (originSection != null) {
            validateDistance(newSection, originSection);
            int sectionIndex = sections.indexOf(originSection);
            Section modifiedSection = new Section(newSection.getDownStationId(), originSection.getDownStationId(),
                    originSection.getDistance() - newSection.getDistance());

            sections.set(sectionIndex, newSection);
            sections.add(sectionIndex + 1, modifiedSection);
            return true;
        }
        return false;
    }

    private boolean addFirstSection(Section newSection) {
        if (newSection.getDownStationId() == sections.get(0).getUpStationId()) {
            sections.addFirst(newSection);
            return true;
        }
        return false;
    }

    private boolean addLastSection(Section newSection) {
        if (newSection.getUpStationId() == sections.get(sections.size() - 1).getDownStationId()) {
            sections.addLast(newSection);
            return true;
        }
        return false;
    }

    private void validateDistance(Section newSection, Section section) {
        if (section.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException("등록하려는 구간의 거리가 더 크거나 같습니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(StationDao.findById(sections.get(FIRST_INDEX).getUpStationId()));
        for(Section section : sections) {
            stations.add(StationDao.findById(section.getDownStationId()));
        }
        return stations;
    }

    public void removeStation(Long stationId) {
        checkOneSection();

        if (removeFirstStation
                (stationId)) return;
        if (removeLastStation
                (stationId)) return;

        removeMiddleStation(stationId);
    }

    private void removeMiddleStation(Long stationId) {
        Section stationIncludedSection = sections.stream()
                .filter(section -> section.getUpStationId() == stationId)
                .findFirst()
                .orElse(null);

        int sectionIndex = sections.indexOf(stationIncludedSection);
        Section prevSection = sections.get(sectionIndex - 1);
        sections.set(sectionIndex - 1, new Section(
                prevSection.getUpStationId(),
                stationIncludedSection.getDownStationId(),
                prevSection.getDistance() + stationIncludedSection.getDistance()));
        sections.remove(sectionIndex);
    }

    private boolean removeLastStation
            (Long stationId) {
        if (sections.get(sections.size() - 1).getDownStationId() == stationId) {
            sections.remove(sections.size() - 1);
            return true;
        }
        return false;
    }

    private boolean removeFirstStation
            (Long stationId) {
        if (sections.get(0).getUpStationId() == stationId) {
            sections.remove(0);
            return true;
        }
        return false;
    }

    private void checkOneSection() {
        if (sections.size() == MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("역이 두개일때는 삭제가 불가능합니다.");
        }
    }
}
