package subway.section;

public enum SectionQuery {
    SAVE("insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)"),
    DELETE_BY_ID("delete from SECTION where id = ?"),
    FIND_BY_LINE_ID("select * from SECTION where line_id = ?"),
    UPDATE("update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?"),
    FIND_BY_STATION_ID_AND_LINE_ID("select * from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)")
    ;

    private SectionQuery(java.lang.String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    private String query;
}
