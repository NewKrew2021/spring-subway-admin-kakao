package subway.line;

public enum SectionType {

    FIRST_STATION,
    UP_STATION,
    DOWN_STATION,
    LAST_STATION,
    EXCEPTION;

    private long id;
    //어디서부터 어디로 갈지
    //어디서 : id
    //어디로 : type 값

    SectionType() {

    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

}
