package subway.line;

public enum SectionType {


    UP_STATION,
    DOWN_STATION,
    FIRST_STATION,
    LAST_STATION,
    EXCEPTION;

    private int index;
    //어디서부터 어디로 갈지
    //어디서 : id
    //어디로 : type 값

    SectionType() {

    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }


}
