package subway.line;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }
    public Long getDownStationId() {
        return downStationId;
    }
    public int getDistance() {
        return distance;
    }

    public SectionType containId(SectionRequest sectionRequest) {
        boolean matchUpStation = this.upStationId.equals(sectionRequest.getUpStationId());
        boolean matchDownStation = this.downStationId.equals(sectionRequest.getDownStationId());
        //둘다 true거나, 둘다 false
        if(matchDownStation == matchUpStation || this.distance <= sectionRequest.getDistance()) {
            return SectionType.EXCEPTION; // 거리체크
        }
        //A B
        //  B C
        //AB B-(10)-C CD DE < linkedlist<sectionRq> <<
        //BF(15m)
        //FB
        //
        // >> BF FC
        // AB BC CD DF FG
        // BF


        // 처음과 끝 검사
        // AB BC CD DE
        // FA, EF

        //FB BF <<
        //AB BF BC CD DE

        //FA EF

        if( matchUpStation ) {
            return SectionType.UP_STATION;
        }
        return SectionType.DOWN_STATION;
    }

//    private boolean isMatchId(long id) {
//        return this.downStationId == id || this.upStationId == id;
//    }


    //1. upStation과 downStation의 id를 Line에서 존재하는지 검색
    //  1.1 둘 다 Line에 존재 하는 경우 추가할 수 없음 < 예외처리
    //  1.2 둘 다 Line에 존재하지 않는 경우도 추가할 수 없음 < 예외처리
    //2. Line에 station을 추가해야하는데  B역, A역, C역 => Line에서 종점변경 필요
    //  2.1 노선에 존재하는 id가 upstation일 경우, 노선에서 해당 upstation의 다음 downstation쪽과 downstation distance 비교
    //      2.1.1 거리가 같을경우 추가 할 수 없음 < 예외처리
    //      2.1.2 line.downstation.distance > downstation.distance 일경우, line의 downstation, upstation 사이에 downstation 추가 < 노선 추가
    //      2.1.3 line.downstation이 비존재인 경우, 그냥 추가

}
