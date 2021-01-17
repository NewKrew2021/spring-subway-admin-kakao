package subway.line;

import java.util.List;

public enum SectionType {


    UP_STATION,         // ordinal == 0
    DOWN_STATION,       //  ordinal == 1
    EXCEPTION;

    private int index;
    //어디서부터 어디로 갈지
    //어디서 : id
    //어디로 : type 값


    public void setIndex(int index) {
        this.index = index;
    }

    public static SectionType setIndex(SectionType sectionType, int index) {
        sectionType.setIndex(index);
        return sectionType;
    }

    public int getIndex() {
        return index;
    }

    public boolean confirmDistance(int distance, List<Section> sections) {
        if( this == UP_STATION ) {
            return distance < sections.get(index).getDownDistance();
        }
        return distance < sections.get(index).getUpDistance();
    }

}



