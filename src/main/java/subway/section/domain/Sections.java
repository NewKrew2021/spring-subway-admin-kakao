package subway.section.domain;

import subway.section.domain.Section;
import subway.section.dto.SectionRequest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;
    private static final int MIN_SECTION_SIZE = 2;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isDuplicatedSection(SectionRequest sectionRequest) {
        // TODO SectionRequest가 아닌 다른 객체를 받아서 검사
        // 이미 존재하는 구간을 넣으려고 할 때를 검사하기위한 메소드
        List<Long> stationsId = getStationsId();
        return stationsId.contains(sectionRequest.getUpStationId())
                && stationsId.contains(sectionRequest.getDownStationId());
    }

    public Section checkAddSection(SectionRequest sectionRequest, long lineId) {
        // TODO SectionRequest가 아닌 다른 객체를 받아서 검사
        if (isDuplicatedSection(sectionRequest)) {
            throw new IllegalArgumentException("같은 구간이 존재합니다.");
        }

        Section standardSection = findSectionToInsert(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        standardSection.sectionConfirm(sectionRequest.getUpStationId());

        int newPosition = standardSection.calculateSectionPosition(sectionRequest.getDistance());
        long stationId = standardSection.chooseInsertSectionStationId(sectionRequest);

        if (checkDistance(standardSection.getPosition(), newPosition)) {
            throw new IllegalArgumentException("추가하려는 구간 사이에 다른 역이 존재합니다.");
        }
        return new Section(lineId, stationId, newPosition);
    }

    public Section findSectionToInsert(long upStationId, long downStationId) {
        return sections
                .stream()
                .filter(section -> section.getStationId() == upStationId || section.getStationId() == downStationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("추가하려는 구간의 역들중 하나는 노선 역들 중 하나 이상은 일치해야 합니다."));
    }

    public boolean checkDistance(int basicDistance, int addDistance) {
        return sections.stream()
                .map(section -> section.isInvalidPositionSection(basicDistance, addDistance))
                .findFirst()
                .orElse(false);
    }

    public List<Long> getStationsId() {
        return sections
                .stream()
                .sorted(Comparator.comparingInt(Section::getPosition))
                .map(Section::getStationId)
                .collect(Collectors.toList());
    }

    public boolean isLeastSizeSections() {
        return sections.size() <= MIN_SECTION_SIZE;
    }

    public boolean hasSection(long stationId) {
        return sections
                .stream()
                .anyMatch(section -> section.getStationId() == stationId);
    }
}
