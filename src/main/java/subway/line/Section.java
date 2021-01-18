package subway.line;

public class Section {

    private int upDistance;
    private long stationId;
    private int downDistance;

    public Section(int upDistance, long stationId, int downDistance) {
        this.upDistance = upDistance;
        this.stationId = stationId;
        this.downDistance = downDistance;
    }

    public SectionType sectionConfirm(long upStationId, long downStationId, int index) {
        if (stationId == upStationId) {
            SectionType sectionType = SectionType.INSERT_DOWN_STATION; // INDEX == SIZE-1 FINAL
            sectionType.setIndex(index);
            return sectionType;
        }
        if (stationId == downStationId) {
            SectionType sectionType = SectionType.INSERT_UP_STATION; // index == 0 FIRST
            sectionType.setIndex(index);
            return sectionType;
        }
        return SectionType.EXCEPTION;
    }

    public int getUpDistance() {
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

    public long getStationId() {
        return stationId;
    }
}

