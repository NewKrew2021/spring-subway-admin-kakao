package subway.line;

import subway.exceptions.exception.SectionDeleteException;
import subway.exceptions.exception.SectionNoStationException;
import subway.exceptions.exception.SectionSameStationException;
import subway.line.section.Section;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Line {
    public static final Long NULL_SECTION_POINT = 0L;
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private Long upStationEndPointId;
    private Long downStationEndPointId;
    private Map<Long, Section> sections;

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationEndPointId = upStationId;
        this.downStationEndPointId = downStationId;
        this.sections = new HashMap<>();
        initializeLine(upStationId, downStationId, distance);
    }

    private void initializeLine(Long upStationId, Long downStationId, int distance) {
        sections.put(NULL_SECTION_POINT, new Section(NULL_SECTION_POINT));
        sections.put(upStationId, new Section(upStationId));
        sections.put(downStationId, new Section(downStationId));
        Section.connectStations(sections.get(NULL_SECTION_POINT), sections.get(upStationId), Integer.MAX_VALUE);
        Section.connectStations(sections.get(downStationId), sections.get(NULL_SECTION_POINT), Integer.MAX_VALUE);
        Section.connectStations(sections.get(upStationId), sections.get(downStationId), distance);
    }

    public void makeSection(Long upStationId, Long downStationId, int distance) {
        if (sections.containsKey(upStationId) && sections.containsKey(downStationId)) {
            throw new SectionSameStationException();
        }

        if (sections.containsKey(upStationId) && sections.get(upStationId).validDownDistance(distance)) {
            sections.put(downStationId, new Section(downStationId));
            Section.connectStations(sections.get(upStationId), sections.get(downStationId), distance);
            updateEndPoints();
            return;
        }

        if (sections.containsKey(downStationId) && sections.get(downStationId).validUpDistance(distance)) {
            sections.put(upStationId, new Section(upStationId));
            Section.connectStations(sections.get(upStationId), sections.get(downStationId), distance);
            updateEndPoints();
            return;
        }

        throw new SectionNoStationException();
    }


    private void updateEndPoints() {
        upStationEndPointId = sections.get(NULL_SECTION_POINT).getDownStationId();
        downStationEndPointId = sections.get(NULL_SECTION_POINT).getUpStationId();
    }

    public void update(LineRequest lineRequest) {
        if (lineRequest.getName() != null) {
            this.name = lineRequest.getName();
        }
        if (lineRequest.getColor() != null) {
            this.color = lineRequest.getColor();
        }
    }

    public List<StationResponse> getStationResponses() {
        Long nowId = upStationEndPointId;
        List<StationResponse> stationResponses = new ArrayList<>();
        while (!stationIsEnd(nowId)) {
            stationResponses.add(new StationResponse(nowId, StationDao.getInstance().getStationById(nowId).getName()));
            nowId = sections.get(nowId).getDownStationId();
        }
        return stationResponses;
    }

    private boolean stationIsEnd(Long nowId) {
        return nowId.equals(NULL_SECTION_POINT);
    }

    public void deleteSection(Long stationId) {
        if (!sections.containsKey(stationId) || sections.get(upStationEndPointId).getDownStationId() == downStationEndPointId) {
            throw new SectionDeleteException();
        }
        sections.get(stationId).deleteSection(); // 소멸자도 만들어야 할까?
        sections.remove(stationId);
        updateEndPoints();
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

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", extraFare=" + extraFare +
                '}';
    }
}
