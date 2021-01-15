package subway.line.section;

import subway.exceptions.exception.SectionNullException;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

public class Section {
    private Long stationId;

    private Section up;
    private Section down;

    private int upDistance;
    private int downDistance;

    public Section(Long stationId) {
        this.stationId = stationId;
        this.up = null;
        this.down = null;
    }
/*
                up                              down
처음              강남                           광교
원하는결과    강남        양재           광교
지금 결과       강남        양재
 */
    public static void connect(Section downStation, Section upStation, int distance) {
        if(upStation.down == null) {
            directConnect(downStation, upStation, distance);
            return;
        }
        else{
                //  원래 강남 --- 광교하고 존재하고 /  현재 들어온게 upStartion이 강남  downStation이 양재인 경우

                // 양재의 down 이 광교가 되어야함
                downStation.down = upStation.down;

                // 강남의 down 이 양재가 되어야함
                upStation.down = downStation;

                // 광교의 up 이 양재가 되어야함
                downStation.down.up = downStation;

                // 양재의 up이 강남이 되어야함
                downStation.up = upStation;
                return;
        }
    }


    // 역 사이를 체크 안하고 직접 연결
    public static void directConnect(Section downSection, Section upSection, int distance) {
        downSection.setUp(upSection);
        downSection.setUpDistance(distance);
        upSection.setDown(downSection);
        upSection.setDownDistance(distance);
    }


    public Station toStation(){
        return StationDao.getInstance().getStationById(stationId);
    }

    public Long getStationId() {
        return stationId;
    }

    public Section getUp() {
        return up;
    }

    public Section getDown() {
        return down;
    }

    public int getUpDistance() {
        return upDistance;
    }

    public int getDownDistance() {
        return downDistance;
    }

    public void setUp(Section up) {
        this.up = up;
    }

    public void setDown(Section down) {
        this.down = down;
    }

    public void setUpDistance(int upDistance) {
        this.upDistance = upDistance;
    }

    public void setDownDistance(int downDistance) {
        this.downDistance = downDistance;
    }

    public StationResponse toStationResponse() {
        Station station = StationDao.getInstance().getStationById(stationId);
        return new StationResponse(station.getId(), station.getName());
    }
}
