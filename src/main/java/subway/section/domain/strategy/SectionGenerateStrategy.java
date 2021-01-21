package subway.section.domain.strategy;


import subway.section.domain.Section;

public interface SectionGenerateStrategy {
    SectionGenerateStrategy make(Section standardSection, Long newStationId, int distance, int nextRelativePosition);
    void distanceValidate(int buildRelativePosition , int nextRelativePosition);
    Section getUpSection();
    Section getDownSection();
    Section getNewSection();
}
