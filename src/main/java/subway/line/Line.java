package subway.line;

import subway.station.StationResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Line {

    private long id;
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

    private Sections sections;

    public Line(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        this.upStationId = lineRequest.getUpStationId();
        this.downStationId = lineRequest.getDownStationId();
        this.distance = lineRequest.getDistance();

        this.sections = new Sections(lineRequest);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public boolean insertSection(SectionRequest sectionRequest) {
        SectionType sectionType = sections.matchStation(sectionRequest); //에러 판정을 확인한다.
        if (sectionType == SectionType.EXCEPTION) {
            return false;
        }
        if (sectionType == SectionType.INSERT_DOWN_STATION || sectionType == SectionType.INSERT_UP_STATION) {
            sections.addSection(sectionType, sectionRequest);
            return true;
        }

        sections.addTerminalSection(sectionType, sectionRequest);
        updateTerminalStation(sectionType, sectionRequest);
        return true;
    }

    private void updateTerminalStation(SectionType sectionType, SectionRequest sectionRequest) {
        if (sectionType == SectionType.INSERT_FIRST_STATION) {
            upStationId = sectionRequest.getUpStationId();
        }

        if (sectionType == SectionType.INSERT_LAST_STATION) {
            downStationId = sectionRequest.getDownStationId();
        }

        distance += sectionRequest.getDistance();
    }

    public List<Long> getStationsId() {
        return sections.getSections()
                .stream()
                .map(Section::getStationId)
                .collect(Collectors.toList());
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

    public void editLine(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }


    public boolean deleteSection(long stationId) {
        // 찾고  : 예외처리 : station 이 없을경우와, sections 의 사이즈가 2인경우
        // 삭제  : 중간역이 빠질 경우, 양 옆이 연결되고 && 종점이 빠질 경우, 다음 역이 종점이 됨
        //      : 수정 사항 -> 삭제 대상 index 와. 종점 여부는 index와 station.size 비교로 알 수 있음
        int index = sections.findDeleteSection(stationId);
        if( index == -1 ) {
            return false;
        }
        sections.deleteSection(index);
        return true;
    }
}
