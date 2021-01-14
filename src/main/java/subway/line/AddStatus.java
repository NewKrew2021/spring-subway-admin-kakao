package subway.line;

public enum AddStatus {

    ADD_INFRONT_UPSTATION, ADD_BEHIND_UPSTATION, ADD_INFRONT_DOWNSTATION, ADD_BEHIND_DOWNSTATION, FAIL;

    public static AddStatus findStatus(Section section, Section newSection) {
        if(section.getUpStationId() == newSection.getDownStationId()) {
            return ADD_INFRONT_UPSTATION;
        }
        if(section.getDownStationId() == newSection.getUpStationId()) {
            return ADD_BEHIND_DOWNSTATION;
        }
        if (section.getUpStationId() == newSection.getUpStationId()) {
            return ADD_BEHIND_UPSTATION;
        }
        if (section.getDownStationId() == newSection.getDownStationId()) {
            return ADD_INFRONT_DOWNSTATION;
        }
        return FAIL;
    }
}
