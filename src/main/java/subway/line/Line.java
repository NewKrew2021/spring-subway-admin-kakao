package subway.line;

import subway.exceptions.InvalidSectionException;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        sections.add(new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
    }

    public boolean containsBothStationsOrNothing(Section newSection) {
        long count = sections.stream()
                .filter(section -> (AddStatus.findStatus(section, newSection) != AddStatus.FAIL))
                .count();
        return ( count % 2 ) == 0 ;
    }

    public List<StationResponse> getStationResponses() {
        List<StationResponse> stationResponses = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            Long stationId = sections.get(i).getUpStationId();
            stationResponses.add(new StationResponse(StationDao.findById(stationId).get()));
        }
        stationResponses.add(
                new StationResponse(StationDao.findById(sections.get(sections.size()-1).getDownStationId()).get())
        );
        return stationResponses;
    }

    public void save(Section newSection) {
        if (containsBothStationsOrNothing(newSection)) {
            throw new InvalidSectionException("두 역이 모두 포함되어 있거나, 두 역 모두 포함되어 있지 않습니다.");
        }
        int selectedIndex = IntStream.range(0, sections.size())
                .filter(i -> AddStatus.findStatus(sections.get(i), newSection) != AddStatus.FAIL)
                .findFirst()
                .getAsInt();

        saveSectionWithStatus(selectedIndex, newSection);
    }

    private void saveSectionWithStatus(int selectedIndex, Section newSection) {
        int distance = sections.get(selectedIndex).getDistance();
        AddStatus addStatus = AddStatus.findStatus(sections.get(selectedIndex), newSection);
        if (addStatus == AddStatus.ADD_INFRONT_UPSTATION) {
            sections.add(0, newSection);
        }
        if (addStatus == AddStatus.ADD_BEHIND_DOWNSTATION) {
            sections.add(sections.size(), newSection);
        }
        validateSectionDistance(newSection, distance);
        if (addStatus == AddStatus.ADD_BEHIND_UPSTATION) {
            sections.set(selectedIndex, new Section(newSection.getDownStationId(), sections.get(selectedIndex).getDownStationId(), distance - newSection.getDistance()));
            sections.add(selectedIndex, new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
        }
        if (addStatus == AddStatus.ADD_INFRONT_DOWNSTATION) {
            sections.set(selectedIndex, new Section(sections.get(selectedIndex).getUpStationId(), newSection.getUpStationId(), distance - newSection.getDistance()));
            sections.add(selectedIndex, new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
        }
    }

    private void validateSectionDistance(Section newSection, int distance) {
        if (distance <= newSection.getDistance()) {
            throw new InvalidSectionException("추가될 구간의 거리가 기존 노선 거리보다 깁니다.");
        }
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
