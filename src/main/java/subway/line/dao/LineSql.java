package subway.line.dao;

public enum LineSql {
    INSERT("insert into LINE (name, color) values (?, ?)"),
    SELECT("select id, name, color from LINE"),
    SELECT_BY_ID("select id, name, color from LINE where id = ?"),
    SELECT_COUNT_BY_NAME("select count(*) from LINE where name = ?"),
    UPDATE("update LINE set name = ?, color = ? where id = ?"),
    DELETE("delete from LINE where id = ?");

    private final String sql;

    LineSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
