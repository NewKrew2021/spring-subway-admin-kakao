package subway.section.domain;

public class UpSideCreator implements SectionCreateStrategy {

    @Override
    public boolean isSupport(Sections sections, SectionCreateValue createValue) {
        return sections.findSectionByStation(createValue.getDownStationId())
                .isPresent();
    }

    @Override
    public Section create(Sections sections, SectionCreateValue createValue) {
        return sections.findSectionByStation(createValue.getDownStationId())
                .map(section -> createNextUpSectionOf(sections, section, createValue))
                .orElseThrow(() -> new AssertionError("isSupport 메서드로 적용 여부를 먼저 확인해야합니다"));
    }

    private Section createNextUpSectionOf(Sections sections, Section section, SectionCreateValue createValue) {
        sections.validateSectionUpDistance(section, createValue.getDistance());

        return new Section(
                createValue.getLineId(),
                createValue.getUpStationId(),
                section.getNextUpPosition(createValue.getDistance())
        );
    }
}
