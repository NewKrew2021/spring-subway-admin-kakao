package subway.section.dao;

public enum SectionSql {
    DELETE_BY_LINE_ID("delete from SECTION where line_id = ?"),
    DELETE_BY_ID("delete from SECTION where id = ?"),
    SELECT_BY_LINE_ID("select * from SECTION where line_id = ?"),
    UPDATE("update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?"),
    INSERT("insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)");

    private String sql;

    SectionSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
