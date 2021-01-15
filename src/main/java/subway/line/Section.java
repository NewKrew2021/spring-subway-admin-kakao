package subway.line;

public class Section {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(){

    }

    public Section(Long upStationId, Long downStationId, int distance){
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(SectionRequest sectionRequest){
        this.upStationId = sectionRequest.getUpStationId();
        this.downStationId = sectionRequest.getDownStationId();
        this.distance = sectionRequest.getDistance();
    }

    public Long getId(){
        return this.id;
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId(){
        return this.downStationId;
    }

    public int getDistance(){
        return this.distance;
    }


    public void combineSection(Section section){
        this.downStationId= section.getDownStationId();
        this.distance+= section.getDistance();
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
