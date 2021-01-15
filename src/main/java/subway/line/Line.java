package subway.line;

import java.util.*;
import java.util.stream.Collectors;

public class Line {

    private long id;
    private String name;
    private String color;
    private long upStationId;//A
    private long downStationId;//F
    private int distance;//24
    private LinkedList<SectionRequest> sectionRequests = new LinkedList<>();


    public Line(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        this.upStationId = lineRequest.getUpStationId();
        this.downStationId = lineRequest.getDownStationId();
        this.distance = lineRequest.getDistance();

        this.sectionRequests.add(new SectionRequest(upStationId, downStationId, distance));
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public SectionType checkSectionType(SectionRequest sectionRequest) {
        List<SectionType> sectionTypes = sectionRequests.stream()
                .map(section -> section.containId(sectionRequest))
                .filter(type -> type != SectionType.EXCEPTION)
                .collect(Collectors.toList());

        if( sectionTypes.size() > 1 ) {
            return SectionType.EXCEPTION; // 중복체크
        }

        if( this.upStationId == sectionRequest.getDownStationId() ) {
            return SectionType.FIRST_STATION;
        }

        if( this.downStationId == sectionRequest.getUpStationId() ) {
            return SectionType.LAST_STATION;
        }

        if( sectionTypes.size() == 0 ) {
            return SectionType.EXCEPTION; // 없는경우
        }

        return sectionTypes.get(0);
    }

    public void addSection(SectionRequest sectionRequest, SectionType sectionType) {
        if (sectionType == SectionType.FIRST_STATION) {
            this.sectionRequests.addFirst(sectionRequest);
            upStationId = sectionRequest.getUpStationId();
            distance += sectionRequest.getDistance();
        }
        else if(sectionType == SectionType.LAST_STATION) {
            this.sectionRequests.addLast(sectionRequest);
            downStationId = sectionRequest.getDownStationId();
            distance += sectionRequest.getDistance();
        }
        else if(sectionType == SectionType.UP_STATION  ) {
            for (int i = 0; i < sectionRequests.size(); i++) {
                if( sectionRequests.get(i).containId(sectionRequest) != SectionType.DOWN_STATION  ) {
                    SectionRequest nextSection = new SectionRequest(sectionRequest.getDownStationId()
                            , sectionRequests.get(i).getDownStationId(), sectionRequests.get(i).getDistance() - sectionRequest.getDistance());
                    sectionRequests.set(i, sectionRequest);
                    sectionRequests.add(i + 1, nextSection);
                    break;
                }
            }
        }
        else {
            for (int i = 0; i < sectionRequests.size(); i++) {
                if( sectionRequests.get(i).containId(sectionRequest) != SectionType.DOWN_STATION  ) {
                    SectionRequest nextSection = new SectionRequest(sectionRequest.getUpStationId()
                            , sectionRequests.get(i).getUpStationId(),  sectionRequests.get(i).getDistance() - sectionRequest.getDistance());
                    sectionRequests.set(i,sectionRequest);
                    sectionRequests.add(i,nextSection);
                    break;
                }
            }
        }


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
}
