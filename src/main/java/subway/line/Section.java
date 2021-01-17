package subway.line;

import subway.station.Station;

public class Section {

    private int upDistance;
    private long stationId;
    private int downDistance;

    // section != section request
    // A B C D
    //  2 3 4
    // 0A2
    // 2B3
    // 3C4
    // 4D0

    public Section( int upDistance, long stationId, int downDistance ) {
        this.upDistance = upDistance;
        this.stationId = stationId;
        this.downDistance = downDistance;
    }

    public SectionType sectionConfirm(long upStationId, long downStationId, int index) {
        if( stationId == upStationId ) {
            return SectionType.setIndex(SectionType.UP_STATION, index);
        }
        if( stationId == downStationId ) {
            return SectionType.setIndex(SectionType.DOWN_STATION, index);
        }
        return SectionType.EXCEPTION;
    }

    public int getUpDistance () {
        return upDistance;
    }

    public int getDownDistance() {
        return downDistance;
    }

    public void setDownDistance(int distance) {
        this.downDistance = distance;
    }

    public void setUpDistance(int distance) {
        this.upDistance = distance;
    }
}

