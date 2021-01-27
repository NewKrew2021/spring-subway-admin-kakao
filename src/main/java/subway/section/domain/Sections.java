package subway.section.domain;

import subway.section.exception.NoStationException;
import subway.section.exception.SectionAlreadyExistException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;
    private static final int MIN_SECTION_SIZE = 2;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isDuplicatedSection(long upStationId, long downStationId) {
        // 이미 존재하는 구간을 넣으려고 할 때를 검사하기위한 메소드
        List<Long> stationsId = getStationsId();
        return stationsId.contains(upStationId)
                && stationsId.contains(downStationId);
    }


    public Section findStandardSection(long upStationId, long downStationId) {
        if (isDuplicatedSection(upStationId, downStationId)) {
            throw new SectionAlreadyExistException();
        }
        return sections
                .stream()
                .filter(section -> section.getStationId() == upStationId || section.getStationId() == downStationId)
                .findFirst()
                .orElseThrow(NoStationException::new);
    }

    public boolean checkDistance(SectionType type, int basicDistance, int addDistance) {
        return sections.stream()
                .map(section -> section.isInvalidPositionSection(type, basicDistance, addDistance))
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
