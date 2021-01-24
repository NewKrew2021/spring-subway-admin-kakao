package subway.line;

public class LineInfoChangedResult {
    private LineInfoChanged status;
    private Long lineId;
    private Long stationId;

    public LineInfoChangedResult(LineInfoChanged status){
        this.status = status;
        this.lineId = -1L;
        this.stationId = -1L;
    }

    public LineInfoChangedResult(LineInfoChanged status, Long lineId, Long stationId) {
        if(status == LineInfoChanged.NONE)
            throw new IllegalArgumentException("잘못된 값입니다.");
        this.status = status;
        this.lineId = lineId;
        this.stationId = stationId;
    }

    public LineInfoChanged getStatus() {
        return status;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getStationId() {
        return stationId;
    }
}
