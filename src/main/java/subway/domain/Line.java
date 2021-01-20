package subway.domain;

public class Line {
    private Long id;
    private final String name;
    private final String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line(Long id, String name, String color, Long upStationId, Long downStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Line(Long id, String name, String color, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        this.upStationId = lineRequest.getUpStationId();
        this.downStationId = lineRequest.getDownStationId();
        this.distance = lineRequest.getDistance();

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }


    public boolean isInsertModified(Section newSection) {
        if(this.downStationId.equals(newSection.getUpStationId())){
            this.downStationId= newSection.getDownStationId();
            return true;
        }
        if(this.upStationId.equals(newSection.getDownStationId())){
            this.upStationId= newSection.getUpStationId();
            return true;
        }
        return false;
    }

    public void lineModifyWhenDelete(Section newSection){
        if(this.upStationId.equals(newSection.getUpStationId())){
            this.upStationId= newSection.getDownStationId();
        }
        if(this.downStationId.equals(newSection.getDownStationId())){
            this.downStationId= newSection.getUpStationId();
        }
    }

}
