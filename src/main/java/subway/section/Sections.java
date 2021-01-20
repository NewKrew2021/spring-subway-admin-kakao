package subway.section;

import subway.exception.exceptions.FailedDeleteSectionException;
import subway.exception.exceptions.FailedSaveSectionException;
import subway.exception.exceptions.InvalidSectionException;

import java.util.List;
import java.util.Optional;

public class Sections {

    private static final long MIN_NECESSARY_SECTION_COUNT = 1;

    private static final String UNABLE_STATION_MESSAGE = "두 역 모두 이미 존재하거나, 모두 포함되어 있지 않습니다.";
    private static final String NOT_INCLUDED_STATION_MESSAGE = "두 역 모두 해당 노선에 포함된 역이 아닙니다.";
    private static final String ALONE_SECTION_MESSAGE = "구간이 하나이기 때문에 삭제할 수 없습니다.";

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public int size() {
        return sections.size();
    }

    public long nextUpStationId(long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == stationId)
                .findFirst()
                .orElseThrow(InvalidSectionException::new)
                .getDownStationId();
    }

    public void validateAlreadyExistBothStationsOrNothing(SectionRequest sectionRequest) {
        long upStationCount = countStationsEqualUpStationOf(sectionRequest);
        long downStationCount = countStationsEqualDownStationOf(sectionRequest);
        if ((upStationCount > 0) == (downStationCount > 0)) {
            throw new InvalidSectionException(UNABLE_STATION_MESSAGE);
        }
    }

    private long countStationsEqualUpStationOf(SectionRequest sectionRequest) {
        return sections.stream()
                .filter(section -> (section.getUpStationId() == sectionRequest.getUpStationId()
                        || section.getDownStationId() == sectionRequest.getUpStationId()))
                .count();
    }

    private long countStationsEqualDownStationOf(SectionRequest sectionRequest) {
        return sections.stream()
                .filter(section -> (section.getUpStationId() == sectionRequest.getDownStationId()
                        || section.getDownStationId() == sectionRequest.getDownStationId()))
                .count();
    }

    public Section getUpdatedSection(SectionRequest sectionRequest) {
        Optional<Section> updatedSection = sections.stream()
                .filter(section -> section.getUpStationId() == sectionRequest.getUpStationId())
                .findFirst();
        if (updatedSection.isPresent()) {
            updatedSection.get().updateUpStationAndDistance(sectionRequest.getDownStationId(), sectionRequest.getDistance());
            return updatedSection.get();
        }
        updatedSection = sections.stream()
                .filter(section -> section.getDownStationId() == sectionRequest.getDownStationId())
                .findFirst();
        if (updatedSection.isPresent()) {
            updatedSection.get().updateDownStationAndDistance(sectionRequest.getUpStationId(), sectionRequest.getDistance());
            return updatedSection.get();
        }
        throw new FailedSaveSectionException(NOT_INCLUDED_STATION_MESSAGE);
    }

    public void validateLineContainsOnlyOneSection() {
        if (size() == MIN_NECESSARY_SECTION_COUNT) {
            throw new InvalidSectionException(ALONE_SECTION_MESSAGE);
        }
    }

    public Section findSectionByUpStationId(long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == stationId)
                .findFirst()
                .orElseThrow(FailedDeleteSectionException::new);
    }

    public Section findSectionByDownStationId(long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId() == stationId)
                .findFirst()
                .orElseThrow(FailedDeleteSectionException::new);
    }
}
