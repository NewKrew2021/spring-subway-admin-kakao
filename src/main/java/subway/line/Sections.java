package subway.line;

import subway.station.StationDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sections {

    private List<Section> sections = new ArrayList<>();

    public Sections(LineRequest lineRequest) {
        this.sections.add(new Section(0, StationDao.findById(lineRequest.getUpStationId()), lineRequest.getDistance()));
        this.sections.add(new Section(lineRequest.getDistance(), StationDao.findById(lineRequest.getDownStationId()), 0));
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
            downDistance = sections.get(index).getDownDistance() != 0 ? sections.get(index).getDownDistance() - sectionRequest.getDistance() : 0;
            stationId = sectionRequest.getDownStationId();
        }
        // sectionRequest 에서 상행인 Section 을 추가하는 경우
        // 해당 Section 의 하행쪽 거리는 sectionRequest 의 거리가 되며, 상행 쪽 거리는 기존 역 거리에서 sectionRequest 의 거리를 뺀 값이다.
        // stationId는 sectionRequest 의 상행 역이 된다.
        if (sectionType == SectionType.INSERT_UP_STATION) {
            upDistance = sections.get(index).getUpDistance() != 0 ? sections.get(index).getUpDistance() - sectionRequest.getDistance() : 0;
            downDistance = sectionRequest.getDistance();
            stationId = sectionRequest.getUpStationId();
        }

        fixSection(index - sectionType.ordinal(), upDistance, downDistance, stationId);
    }

    // 0A2 2B3 3C4 4D5 5E0
    //FA 1

    //0F1 1A2 2B3 3C4 4D5 5E0
    //index -> -1

    // 0A2 2B3 3C4 4D5 5E0  < 5
    // EF 1
    // 0A2 2B3 3C4 4D5 5E1 1F0
    //

    // 0A4 4B5 5C6 6D0
    // BF 1
    // 0A4 4B1 1F4 4C6 6D0


    public void fixSection(int index, int upDistance, int downDistance, long id) {

        sections.get(index).setDownDistance(upDistance);
        sections.get(index + 1).setUpDistance(downDistance);

        Section section = new Section(upDistance, StationDao.findById(id), downDistance);
        sections.add(index + 1, section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addTerminalSection(SectionType sectionType, SectionRequest sectionRequest) {
        if (sectionType == SectionType.INSERT_FIRST_STATION) {
            sections.get(0).setUpDistance(sectionRequest.getDistance());
            sections.add(0, new Section(
                    0,
                    StationDao.findById(sectionRequest.getUpStationId()),
                    sectionRequest.getDistance()));
        }

        if (sectionType == SectionType.INSERT_LAST_STATION) {
            sections.get(sections.size() - 1).setDownDistance(sectionRequest.getDistance());
            sections.add(sections.size(), new Section(
                    sectionRequest.getDistance(),
                    StationDao.findById(sectionRequest.getDownStationId()),
                    0));
        }
    }
}
