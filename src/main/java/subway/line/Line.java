package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.exceptions.InvalidLineArgumentException;
import subway.exceptions.InvalidSectionException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    // 1section.getUpStation == newSection.getUpStation -> 상행역의 뒤에 끼워넣음
    // 2setcion.getDownStation == newSection.getDownStation -> 하행역의 앞에 끼워넣음
    // 3section.getUpStation == newSection.getDownStation -> 상행역의 앞에 끼워넣음
    // 4section.getDownStation == newSection.getUpStation -> 히헹역의 뒤에 끼워넣음
    // 5NOTHING
    public AddStatus findStatus(Section section, Section newSection) {
        if(section.getUpStationId() == newSection.getDownStationId()) {
            return AddStatus.ADD_INFRONT_UPSTATION;
        }
        if(section.getDownStationId() == newSection.getUpStationId()) {
            return AddStatus.ADD_BEHIND_DOWNSTATION;
        }
        if (section.getUpStationId() == newSection.getUpStationId()) {
            return AddStatus.ADD_BEHIND_UPSTATION;
        }
        if (section.getDownStationId() == newSection.getDownStationId()) {
            return AddStatus.ADD_INFRONT_DOWNSTATION;
        }
        return AddStatus.FAIL;
    }

    public boolean containsBothStationsOrNothing(Section newSection) {
        long count = sections.stream()
                .filter(section -> (findStatus(section, newSection) != AddStatus.FAIL))
                .count();
        return ( count % 2 ) == 0 ;
    }

    public void save(Section newSection) {
        if (containsBothStationsOrNothing(newSection)) {
            throw new InvalidSectionException("두 역이 모두 포함되어 있거나, 두 역 모두 포함되어 있지 않습니다.");
        }

        for (int i = 0; i < sections.size(); i++) {
            AddStatus addStatus = findStatus(sections.get(i), newSection);
            if(addStatus == addStatus.ADD_BEHIND_DOWNSTATION) {
                if(i != sections.size()-1) {
                    Field field = ReflectionUtils.findField(Section.class, "upStationId");
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, sections.get(i+1), newSection.getDownStationId());
                }
                sections.add(i+1, newSection);
                break;
            }
            if (addStatus == AddStatus.ADD_INFRONT_UPSTATION) {
                if(i != 0) {
                    Field field = ReflectionUtils.findField(Section.class, "upStationId");
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, sections.get(i-1), newSection.getUpStationId());
                }
                sections.add(i, newSection);
                break;
            }
            if (addStatus == AddStatus.ADD_BEHIND_UPSTATION) {
                int distance = sections.get(i).getDistance();
                if(distance <= newSection.getDistance()) {
                    throw new InvalidLineArgumentException("추가될 구간의 거리가 기존 노선 거리보다 깁니다.");
                }
                sections.set(i, new Section(newSection.getDownStationId(), sections.get(i).getDownStationId(), distance - newSection.getDistance()));
                sections.add(i, new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
                break;
            }
            if (addStatus == AddStatus.ADD_INFRONT_DOWNSTATION) {
                int distance = sections.get(i).getDistance();
                if (distance <= newSection.getDistance()) {
                    throw new InvalidLineArgumentException("추가될 구간의 거리가 기존 노선 거리보다 깁니다.");
                }
                sections.set(i, new Section(sections.get(i).getUpStationId(), newSection.getUpStationId(), distance - newSection.getDistance()));
                sections.add(i, new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
                break;
            }
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
