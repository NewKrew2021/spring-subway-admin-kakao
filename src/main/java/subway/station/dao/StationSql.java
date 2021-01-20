package subway.station.dao;

public enum StationSql {
    INSERT("insert into STATION (name) values (?)"),
    SELECT("select id, name from STATION"),
    DELETE_BY_ID("delete from STATION where id = ?"),
    SELECT_BY_ID("select id, name from STATION where id = ?");

    private String sql;

    StationSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
