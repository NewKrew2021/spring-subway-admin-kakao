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
    public static final Long NULL_SECTION_POINT = 0L; // 일급 컬렉션으로 만드는 것이 좋아보임.
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private Long upStationEndPointId;
    private Long downStationEndPointId;
    private Map<Long, Section> sections; //일급 컬렉션으로 만드는 것이 좋아보임.

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
        if (isThereTwoStations(upStationId, downStationId)) {
            throw new SectionSameStationException();
        }

        if (canMakeDownSection(upStationId, distance)) {
            sections.put(downStationId, new Section(downStationId));
            connect(upStationId, downStationId, distance);
            return;
        }

        if (canMakeUpSection(downStationId, distance)) {
            sections.put(upStationId, new Section(upStationId));
            connect(upStationId, downStationId, distance);
            return;
        }

        throw new SectionNoStationException();
    }

    private boolean canMakeUpSection(Long downStationId, int distance) {
        return sections.containsKey(downStationId) && sections.get(downStationId).validUpDistance(distance);
    }

    private boolean canMakeDownSection(Long upStationId, int distance) {
        return sections.containsKey(upStationId) && sections.get(upStationId).validDownDistance(distance);
    }

    private boolean isThereTwoStations(Long stationId1, Long stationId2) {
        return sections.containsKey(stationId1) && sections.containsKey(stationId2);
    }

    private void connect(Long upStationId, Long downStationId, int distance) {
        Section.connectStations(sections.get(upStationId), sections.get(downStationId), distance);
        updateEndPoints();
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

    public void deleteSection(Long stationId) {
        if (!sections.containsKey(stationId) || areThereOnlyTwoStations()) {
            throw new SectionDeleteException();
        }
        sections.get(stationId).deleteSection();
        sections.remove(stationId);
        updateEndPoints();
    }

    private boolean areThereOnlyTwoStations() {
        return sections.get(upStationEndPointId).getDownStationId().equals(downStationEndPointId);
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
