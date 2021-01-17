package subway.line;

import java.util.List;

public enum SectionType {


    INSERT_DOWN_STATION,         // ordinal == 0
    INSERT_UP_STATION,       //  ordinal == 1
    INSERT_FIRST_STATION,
    INSERT_LAST_STATION,
    EXCEPTION;


    private int index;


    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public boolean invalidateDistance(int distance, List<Section> sections) {
        if(this == INSERT_FIRST_STATION || this == INSERT_LAST_STATION){
            return false;
        }
        if( this == INSERT_DOWN_STATION) {
            return distance >= sections.get(index).getDownDistance();
        }
        return distance >= sections.get(index).getUpDistance();
    }

}



