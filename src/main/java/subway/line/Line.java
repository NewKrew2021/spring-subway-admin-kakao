package subway.line;

import subway.line.section.Section;
import subway.line.section.SectionController;
import subway.station.Station;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Line {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private Long upStationId;
    private Long downStationId;
    private List<Station> stations;
    private Map<Long, Section> sections;

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.stations = new ArrayList<>();
        this.sections = new HashMap<>();
        Section.connect(getOrCreateSection(downStationId), getOrCreateSection(upStationId), distance);
    }

    private Section getOrCreateSection(Long stationId) {
        if(!sections.containsKey(stationId))
        {
           sections.put(stationId, new Section(stationId));
        }
        return sections.get(stationId);
    }

    public void update(LineRequest lineRequest) {
        if (lineRequest.getName() != null) {
            this.name = lineRequest.getName();
        }
        if (lineRequest.getColor() != null) {
            this.color = lineRequest.getColor();
        }
    }

    //상행 종점에서 하행 종점까지 순서대로 반
    public List<StationResponse> getStationResponses() {
        List<StationResponse> tmpStationResponses = new ArrayList<>();

        Section head = sections.get(upStationId);
        while (head != null) {
            tmpStationResponses.add(head.toStationResponse());
            head = head.getDown();
        }
        return tmpStationResponses;
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

    public int getExtraFare() {
        return extraFare;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", extraFare=" + extraFare +
                '}';
    }

    public void connectSection(Long downStationId, Long upStationId, int distance) {
        Section.connect(getOrCreateSection(downStationId), getOrCreateSection(upStationId), distance);
    }
}
