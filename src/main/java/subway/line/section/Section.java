package subway.line.section;

import subway.exceptions.exception.SectionSameStationException;

public class Section {
    private Long stationId;

    private Section myUpStation;
    private Section myDownStation;

    private Distance upDistance;
    private Distance downDistance;

    static public void connectStations(Section upSection, Section downSection, int distance) {

        if (upSection.myDownStation != null && downSection.myUpStation != null) {
            throw new SectionSameStationException();
        }

        if (upSection.myDownStation == null && downSection.myUpStation == null) {
            directConnect(upSection, downSection, distance);
            return;
        }

        if (upSection.myDownStation != null) {
            directConnect(downSection, upSection.myDownStation, upSection.downDistance.calculateDistance(distance));
            directConnect(upSection, downSection, distance);
            return;
        }

        if (downSection.myUpStation != null) {
            directConnect(downSection.myUpStation, upSection, downSection.upDistance.calculateDistance(distance));
            directConnect(upSection, downSection, distance);
        }
    }

    static private void directConnect(Section upSection, Section downSection, int distance) {
        upSection.myDownStation = downSection;
        upSection.downDistance.setDistance(distance);
        downSection.myUpStation = upSection;
        downSection.upDistance.setDistance(distance);
    }


    public Section(Long stationId) {
        this.stationId = stationId;
        this.myUpStation = null;
        this.myDownStation = null;
        this.upDistance = new Distance();
        this.downDistance = new Distance();
    }


    public boolean validDownDistance(int distance) {
        return this.downDistance.validateDistance(distance);
    }

    public boolean validUpDistance(int distance) {
        return this.upDistance.validateDistance(distance);
    }

    public Long getDownStationId() {
        return this.myDownStation.getStationId();
    }

    public Long getUpStationId() {
        return this.myUpStation.getStationId();
    }

    public Long getStationId() {
        return stationId;
    }

    public void deleteSection() {
        int distance = Distance.addDistance(upDistance, downDistance);
        directConnect(myUpStation, myDownStation, distance);
    }
}
