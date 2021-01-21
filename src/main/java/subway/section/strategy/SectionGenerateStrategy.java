package subway.section.strategy;


import subway.section.Section;

public interface SectionGenerateStrategy {
    SectionGenerateStrategy make(Section standardSection, Long newStationId, int distance, int nextRelativePosition);
    void distanceValidate(int buildRelativePosition , int nextRelativePosition);
    Section getUpSection();
    Section getDownSection();
    Section getNewSection();
}
