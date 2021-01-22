package subway.section;

import subway.exceptions.IllegalSectionMerge;

public class SectionPair {
    private Section first, second;

    public SectionPair(Section first, Section second) {
        this.first = first;
        this.second = second;
    }

    public Section merge() {
        validateMergeAndRearrange();
        Long newUpStaionId = first.getUpStationId();
        Long newDownStationId = second.getDownStationId();

        return new Section(
                first.getLineId(),
                newUpStaionId,
                newDownStationId,
                (first.getDistance() + second.getDistance())
        );
    }

    private void validateMergeAndRearrange() {
        if(first.getDownStationId().equals(second.getUpStationId())) return;
        if(second.getDownStationId().equals(first.getUpStationId())) {
            Section tmp = first;
            first = second;
            second = tmp;
            return;
        }
        throw new IllegalSectionMerge("두 section을 merge할 수 없습니다.");
    }
}
