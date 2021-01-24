package subway.station.dao;

public enum StationQuery {
    SAVE("insert into STATION (name) values (?)"),
    FIND_ALL("select id, name from STATION"),
    FIND_BY_ID("select id, name from STATION where id = ?"),
    FIND_BY_NAME("select id, name from STATION where name = ?"),
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
