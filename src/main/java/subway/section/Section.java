package subway.section;

import subway.exception.NotExistSectionDeleteException;
import subway.exception.TooFewSectionAsDeleteException;

import java.util.Objects;

public class Section {

    public static final Long WRONG_ID = -1L;
    public static final Section DO_NOT_EXIST_SECTION = new Section(-1L, -1L, -1L, -1, -1L);
    private static final int MIN_SECTION_COUNT_OF_LINE = 2;

    private Long id;
    private Long lineId;
    private Long stationId;
    private int distance;
    private Long nextStationId;

    public Section(Long id, Long lineId, Long stationId, int distance, Long nextStationId) {
        this.id = id;
        this.lineId = lineId;
        this.stationId = stationId;
        this.distance = distance;
        this.nextStationId = nextStationId;
    }

    public Section(Long lineId, Long stationId, int distance, Long nextStationId) {
        this(null, lineId, stationId, distance, nextStationId);
    }

    public void updateNextStationToOtherNextStation(Section section) {
        if( this != Section.DO_NOT_EXIST_SECTION ) {
            this.setNextStation(section.getNextStationId());
        }
    }

    public void updateNextSectionToOtherStation(Section section) {
        if( this != Section.DO_NOT_EXIST_SECTION ) {
            this.setNextStation(section.getStationId());
        }
    }

    public boolean isPossibleDelete(int count) {
        if ( this == DO_NOT_EXIST_SECTION ) {
            throw new NotExistSectionDeleteException();
        }
        if ( count <= MIN_SECTION_COUNT_OF_LINE) {
            throw new TooFewSectionAsDeleteException();
        }
        return true;
    }

    public boolean isExist() {
        return this != DO_NOT_EXIST_SECTION;
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getNextStationId() {
        return nextStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setNextStation(Long nextId) {
        this.nextStationId = nextId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId) && Objects.equals(stationId, section.stationId) && Objects.equals(nextStationId, section.nextStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, stationId, distance, nextStationId);
    }
}

