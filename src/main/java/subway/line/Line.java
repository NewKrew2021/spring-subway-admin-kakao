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

    public Line(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    private boolean isDeleteStationIsFrontOrRear(Long stationId) {
        return stationId == sections.get(0).getUpStationId() || stationId == sections.get(sections.size()-1).getDownStationId();
    }

    private void deleteFrontOrRearStation(Long stationId) {
        if (stationId == sections.get(0).getUpStationId()) {
            sections.remove(0);
            return;
        }
        sections.remove(sections.size()-1);
    }

    private void deleteStationById(Long stationId) {
        int selectedIndex = IntStream.range(1, sections.size())
                .filter(i -> sections.get(i).getUpStationId() == stationId)
                .findFirst()
                .getAsInt();

        deleteStationAndMergeSection(selectedIndex);
    }

    private void deleteStationAndMergeSection(int selectedIndex) {
        long upStationId = sections.get(selectedIndex - 1).getUpStationId();
        long downStationId = sections.get(selectedIndex).getDownStationId();
        int distance = sections.get(selectedIndex - 1).getDistance() + sections.get(selectedIndex).getDistance();
        sections.set(selectedIndex - 1, new Section(upStationId, downStationId, distance));
        sections.remove(selectedIndex);
    }

    public void delete(Long stationId) {
        if (sections.size() == 1) {
            throw new InvalidSectionException("구간이 하나이기 때문에 삭제할 수 없습니다.");
        }
        if (isDeleteStationIsFrontOrRear(stationId)) {
            deleteFrontOrRearStation(stationId);
            return;
        }
        deleteStationById(stationId);
    }

    public void save(Section newSection) {
        if (containsBothStationsOrNothing(newSection)) {
            throw new InvalidSectionException("두 역이 모두 포함되어 있거나, 두 역 모두 포함되어 있지 않습니다.");
        }
        saveSection(newSection);
    }

    private boolean containsBothStationsOrNothing(Section newSection) {
        boolean containsUpStation = sections.stream()
                .anyMatch(section -> section.contains(newSection.getUpStationId()));
        boolean containsDownStation = sections.stream()
                .anyMatch(section -> section.contains(newSection.getDownStationId()));
        return containsDownStation == containsUpStation;
    }

    private boolean newSectionIsFront(Section newSection) {
        long id = sections.get(0).getUpStationId();
        return id == newSection.getUpStationId() || id == newSection.getDownStationId();
    }

    private void saveNewSectionFront(Section newSection) {
        if (sections.get(0).getUpStationId() == newSection.getDownStationId()) {
            sections.add(0, newSection);
            return;
        }
        saveNewSectionBasedUpStation(newSection, 0);
    }

    private void saveNewSectionBasedUpStation(Section newSection, int selectedIndex) {
        int distance = sections.get(selectedIndex).getDistance();
        validateSectionDistance(newSection, distance);
        long prevDownStationId = sections.get(selectedIndex).getDownStationId();
        sections.set(selectedIndex, new Section(newSection.getDownStationId(), prevDownStationId, distance - newSection.getDistance()));
        sections.add(selectedIndex, newSection);
    }

    private boolean newSectionIsRear(Section newSection) {
        long id = sections.get(sections.size()-1).getDownStationId();
        return id == newSection.getUpStationId() || id == newSection.getDownStationId();
    }

    private void saveNewSectionRear(Section newSection) {
        if (sections.get(sections.size()-1).getDownStationId() == newSection.getUpStationId()) {
            sections.add(newSection);
            return;
        }
        saveNewSectionBasedDownStation(newSection, sections.size()-1);
    }

    private void saveNewSectionBasedDownStation(Section newSection, int selectedIndex) {
        int distance = sections.get(selectedIndex).getDistance();
        long prevUpStationId = sections.get(selectedIndex).getUpStationId();
        validateSectionDistance(newSection, distance);
        sections.set(selectedIndex, newSection);
        sections.add(selectedIndex, new Section(prevUpStationId, newSection.getUpStationId(), distance - newSection.getDistance()));
    }

    private boolean isNewSectionIsFrontOrRear(Section newSection) {
        return newSectionIsFront(newSection) || newSectionIsRear(newSection);
    }

    private void saveNewSectionFrontOrRear(Section newSection) {
        if (newSectionIsFront(newSection)) {
            saveNewSectionFront(newSection);
            return;
        }
        saveNewSectionRear(newSection);
    }

    private void saveSection(Section newSection) {
        if(isNewSectionIsFrontOrRear(newSection)) {
            saveNewSectionFrontOrRear(newSection);
            return;
        }
        int selectedIndex = findUpStationIdFromSections(newSection.getUpStationId());
        if (selectedIndex == -1) {
            selectedIndex = findDownStationIdFromSections(newSection.getDownStationId());
            saveNewSectionBasedDownStation(newSection, selectedIndex);
            return;
        }
        saveNewSectionBasedUpStation(newSection, selectedIndex);
    }

    private int findUpStationIdFromSections(Long newSectionUpStationId) {
        return IntStream.range(0, sections.size())
                .filter(i -> sections.get(i).getUpStationId() == newSectionUpStationId)
                .findFirst()
                .orElse(-1);
    }

    private int findDownStationIdFromSections(Long newSectionDownStationId) {
        return IntStream.range(0, sections.size())
                .filter(i->sections.get(i).getDownStationId() == newSectionDownStationId)
                .findFirst()
                .getAsInt();
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
