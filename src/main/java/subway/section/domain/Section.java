package subway.section.domain;

import subway.section.dto.SectionRequest;

import java.util.Objects;

public class Section {

    private long id;
    private final long stationId;
    private final long lineId;
    private final int position;
    private SectionType sectionType;
    // TODO SectionType 삭제가 필요함

    public Section(long id, long stationId, long lineId, int position) {
        this.id = id;
        this.stationId = stationId;
        this.lineId = lineId;
        this.position = position;
    }

    public Section(long lineId, long stationId, int position) {
        this.lineId = lineId;
        this.stationId = stationId;
        this.position = position;
    }

    public void sectionConfirm(long upStationId) {
        if (stationId == upStationId) {
            sectionType = SectionType.INSERT_DOWN_STATION;
            return;
        }
        sectionType = SectionType.INSERT_UP_STATION;
    }

    public int calculateSectionPosition(int position){
        if(sectionType == SectionType.INSERT_UP_STATION){
            return this.position - position;
        }
        return this.position + position;
    }

    public long chooseInsertSectionStationId(SectionRequest sectionRequest){
        if(sectionType == SectionType.INSERT_UP_STATION){
            return sectionRequest.getUpStationId();
        }
        return sectionRequest.getDownStationId();
    }

    public boolean isInvalidPositionSection(int basicPosition, int newPosition) {
        if (sectionType == SectionType.INSERT_UP_STATION) {
            return basicPosition <= position && position <= newPosition;
        }
        return newPosition <= position && position <= basicPosition;
    }

    public long getStationId() {
        return stationId;
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return stationId == section.stationId && lineId == section.lineId && position == section.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, lineId, position);
    }
}

