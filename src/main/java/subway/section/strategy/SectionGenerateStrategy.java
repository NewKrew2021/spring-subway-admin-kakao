package subway.section.strategy;


import subway.section.Section;

public interface SectionGenerateStrategy {
    SectionGenerateStrategy make(Section standardSection, Long newStationId, int distance);
    Section getUpSection();
    Section getDownSection();
    Section getNewSection();

}
