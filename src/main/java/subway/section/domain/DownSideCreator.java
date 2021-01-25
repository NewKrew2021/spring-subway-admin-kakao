package subway.section.domain;

public class DownSideCreator implements SectionCreateStrategy {

    @Override
    public boolean isSupport(Sections sections, SectionCreateValue createValue) {
        return sections.findSectionByStation(createValue.getUpStationId())
                .isPresent();
    }

    @Override
    public Section create(Sections sections, SectionCreateValue createValue) {
        return sections.findSectionByStation(createValue.getUpStationId())
                .map(section -> createNextDownSectionOf(sections, section, createValue))
                .orElseThrow(() -> new AssertionError("isSupport 메서드로 적용 여부를 먼저 확인해야합니다"));
    }

    private Section createNextDownSectionOf(Sections sections, Section section, SectionCreateValue createValue) {
        sections.validateSectionDownDistance(section, createValue.getDistance());

        return new Section(
                createValue.getLineId(),
                createValue.getDownStationId(),
                section.getNextDownPosition(createValue.getDistance())
        );
    }
}
