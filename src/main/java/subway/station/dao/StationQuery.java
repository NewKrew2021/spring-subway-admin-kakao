package subway.station.dao;

public enum StationQuery {
    SAVE("insert into STATION (name) values (?)"),
    FIND_ALL("select * from STATION"),
    FIND_BY_ID("select * from STATION where id = ?"),
    FIND_BY_NAME("select * from STATION where name = ?"),
    DELETE_BY_ID("delete from STATION where id = ?"),
    ;


    private StationQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    private String query;
}
