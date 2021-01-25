package subway.line.dao;

public enum LineQuery {
    SAVE("insert into LINE (name, color, up_station_id, down_station_id, distance) values (?, ?, ?, ?, ?)"),
    FIND_ALL("select id, name, color, up_station_id, down_station_id, distance from LINE"),
    DELETE_BY_ID("delete from LINE where id = ?"),
    FIND_BY_ID("select id, name, color, up_station_id, down_station_id, distance from LINE where id = ?"),
    FIND_BY_NAME("select id, name, color, up_station_id, down_station_id, distance from LINE where name = ?"),
    UPDATE("update LINE set up_station_id = ?, down_station_id = ?, distance = ? where id = ?");


    private LineQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    private String query;
}
