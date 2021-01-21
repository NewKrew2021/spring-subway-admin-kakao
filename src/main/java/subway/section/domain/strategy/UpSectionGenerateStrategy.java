package subway.section.domain.strategy;

import subway.exceptions.sectionExceptions.SectionIllegalDistanceException;
import subway.section.domain.Section;

public class UpSectionGenerateStrategy implements SectionGenerateStrategy {
    private Section downSection;
    private Section upSection;

    public UpSectionGenerateStrategy() {
    }

    public UpSectionGenerateStrategy(Section standardSection, Section newSection) {
        this.downSection = standardSection;
        this.upSection = newSection;
    }

    @Override
    public Section getNewSection() {
        return upSection;
    }

    @Override
    public Section getUpSection() {
        return upSection;
    }

    @Override
    public Section getDownSection() {
        return downSection;
    }

    @Override
    public SectionGenerateStrategy make(Section standardSection, Long newStationId, int distance, int nextRelativePosition) {
        distanceValidate(standardSection.getRelativePosition() - distance , nextRelativePosition);
        return new UpSectionGenerateStrategy(
                standardSection,
                new Section(
                        standardSection.getLineId(),
                        newStationId,
                        standardSection.getRelativePosition() - distance)
        );
    }

    @Override
    public void distanceValidate(int buildRelativePosition , int nextRelativePosition) {
        if(buildRelativePosition <= nextRelativePosition ) {
            throw new SectionIllegalDistanceException();
        }
    }

}
