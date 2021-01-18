package subway.section;

import subway.line.LineRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sections {

    private List<Section> sections = new ArrayList<>();

    public Sections(LineRequest lineRequest) {
        this.sections.add(new Section(0, lineRequest.getUpStationId(), lineRequest.getDistance()));
        this.sections.add(new Section(lineRequest.getDistance(), lineRequest.getDownStationId(), 0));
    }

    public SectionType matchStation(SectionRequest sectionRequest) {
        int distance = sectionRequest.getDistance();
        List<SectionType> sectionTypes = confirmSectionTypes(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());

        if (sectionTypes.get(0).getIndex() == 0
                && sectionTypes.get(0) == SectionType.INSERT_UP_STATION) {
            sectionTypes.set(0, SectionType.INSERT_FIRST_STATION);
        }

        if (sectionTypes.get(0).getIndex() == sections.size() - 1
                && sectionTypes.get(0) == SectionType.INSERT_DOWN_STATION) {
            sectionTypes.set(0, SectionType.INSERT_LAST_STATION);
        }

        if (sectionTypes.size() != 1 || sectionTypes.get(0).invalidateDistance(distance, sections)) {
            return SectionType.EXCEPTION;
        }

        return sectionTypes.get(0);
    }

    private List<SectionType> confirmSectionTypes(long upStationId, long downStationId) {
        return IntStream.range(0, sections.size())
                .mapToObj(i -> sections.get(i).sectionConfirm(upStationId, downStationId, i))
                .filter(sectionType -> sectionType != SectionType.EXCEPTION)
                .collect(Collectors.toList());
    }

    public void addSection(SectionType sectionType, SectionRequest sectionRequest) {
        int upDistance = 0, downDistance = 0;
        int index = sectionType.getIndex();
        long stationId = 0;

        // 추가할 Section 을 선언한다.
        // Section 의 상행 Distance, 하행 Distance, Station id는 추가할 역이 상행인지 하행인지에 따라 값이 다르게 선언된다.

        // sectionRequest 에서 하행인 Section 을 추가하는 경우
        // 해당 Section 의 상행쪽 거리는 sectionRequest 의 거리가 되며, 하행 쪽 거리는 기존 역 거리에서 sectionRequest 의 거리를 뺀 값이다.
        // stationId는 sectionRequest 의 하행 역이 된다.
        if (sectionType == SectionType.INSERT_DOWN_STATION) {
            upDistance = sectionRequest.getDistance();
            downDistance = sections.get(index).getDownDistance() - sectionRequest.getDistance();
            stationId = sectionRequest.getDownStationId();
        }
        // sectionRequest 에서 상행인 Section 을 추가하는 경우
        // 해당 Section 의 하행쪽 거리는 sectionRequest 의 거리가 되며, 상행 쪽 거리는 기존 역 거리에서 sectionRequest 의 거리를 뺀 값이다.
        // stationId는 sectionRequest 의 상행 역이 된다.
        if (sectionType == SectionType.INSERT_UP_STATION) {
            upDistance = sections.get(index).getUpDistance() - sectionRequest.getDistance();
            downDistance = sectionRequest.getDistance();
            stationId = sectionRequest.getUpStationId();
        }

        Section section = new Section(upDistance, stationId, downDistance);
        fixSection(sectionType.getIndex() - sectionType.ordinal(), section);
    }

    public void fixSection(int index, Section section) {
        sections.get(index).setDownDistance(section.getUpDistance());
        sections.get(index + 1).setUpDistance(section.getDownDistance());
        sections.add(index + 1, section);
    }

    public void addTerminalSection(SectionType sectionType, SectionRequest sectionRequest) {
        if (sectionType == SectionType.INSERT_FIRST_STATION) {
            sections.get(0).setUpDistance(sectionRequest.getDistance());
            sections.add(0, new Section(
                    0,
                    sectionRequest.getUpStationId(),
                    sectionRequest.getDistance()));
        }

        if (sectionType == SectionType.INSERT_LAST_STATION) {
            sections.get(sections.size() - 1).setDownDistance(sectionRequest.getDistance());
            sections.add(sections.size(), new Section(
                    sectionRequest.getDistance(),
                    sectionRequest.getDownStationId(),
                    0));
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    // 0개 일 경우
    public int findDeleteSection(long stationId) {
        if (sections.size() == 2) {
            return -1;
        }
        return IntStream.range(0, sections.size())
                .filter(i -> sections.get(i).getStationId() == stationId)
                .findFirst()
                .orElse(-1);
    }

    public void deleteSection(int index) {
        if (index == 0) { //first station
            sections.get(index + 1).setUpDistance(0);
        }
        if (index == sections.size() - 1) { // last station
            sections.get(index - 1).setDownDistance(0);
        }
        if (index != 0 && index != sections.size() - 1) {
            int distance = sections.get(index).getDownDistance() + sections.get(index).getUpDistance();
            sections.get(index - 1).setDownDistance(distance);
            sections.get(index + 1).setUpDistance(distance);
        }
        sections.remove(index); //지우기
    }
}
