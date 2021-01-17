package subway.line;

import subway.station.Station;
import subway.station.StationResponse;

public class Section {

    private int upDistance;
    private Station station;
    private int downDistance;

    public Section(int upDistance, Station station, int downDistance ) {
        this.upDistance = upDistance;
        this.station = station;
        this.downDistance = downDistance;
    }

    public StationResponse convertStationResponse() {
        return new StationResponse(station.getId(), station.getName());
    }

//    public Section(SectionType type, int upDistance, int downDistance, long stationId){
//        if(type == SectionType.INSERT_UP_STATION){
//            this(up)
//        }
//    }

    //A B C D E
    //FA

    public SectionType sectionConfirm(long upStationId, long downStationId, int index) {
        if( station.getId() == upStationId ) {
            SectionType sectionType = SectionType.INSERT_DOWN_STATION; // INDEX == SIZE-1 FINAL
            sectionType.setIndex(index);
            return sectionType;
        }
        if( station.getId() == downStationId ) {
            SectionType sectionType = SectionType.INSERT_UP_STATION; // index == 0 FIRST
            sectionType.setIndex(index);
            return sectionType;
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

