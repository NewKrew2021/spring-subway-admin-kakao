package subway.section.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {

    private final int INIT_STATION_ID = 0;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Map<Long, Section> getOrderedSections() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
    }

    public Map<Long, Section> getReverseOrderedSections() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, section -> section));
    }

    public Section merge(Long stationId) {
        Section frontSection = this.getSections().get(0);
        Section backSection = this.getSections().get(1);

        long upStationId = INIT_STATION_ID;
        long downStationId = INIT_STATION_ID;

        if (frontSection.getUpStationId() == stationId) {
            upStationId = backSection.getUpStationId();
            downStationId = frontSection.getDownStationId();
        }

        if (frontSection.getDownStationId() == stationId) {
            upStationId = frontSection.getUpStationId();
            downStationId = backSection.getDownStationId();
        }

        return new Section(frontSection.getLineId(), upStationId, downStationId,
                frontSection.getDistance() + backSection.getDistance());
    }

    public int size() {
        return sections.size();
    }

    public Section get(int index) {
        return sections.get(index);
    }

    public Long getLineId() {
        return sections.get(0).getLineId();
    }
}
