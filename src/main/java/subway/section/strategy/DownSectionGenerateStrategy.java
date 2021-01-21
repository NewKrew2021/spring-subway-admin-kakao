package subway.section.strategy;

import subway.section.Section;

public class DownSectionGenerateStrategy implements SectionGenerateStrategy {
    private Section upSection;
    private Section downSection;

    public DownSectionGenerateStrategy(){
    }

    public DownSectionGenerateStrategy(Section standardSection, Section newSection) {
        this.upSection = standardSection;
        this.downSection = newSection;
    }

    @Override
    public Section getNewSection() {
        return downSection;
    }

    @Override
    public Section getDownSection() {
        return downSection;
    }

    @Override
    public Section getUpSection() {
        return upSection;
    }

    @Override
    public SectionGenerateStrategy make(Section standardSection, Long newStationId, int distance) {
        return new DownSectionGenerateStrategy(
                standardSection,
                new Section(
                        standardSection.getLineId(),
                        newStationId,
                        standardSection.getRelativePosition() + distance)
        );
    }

}
