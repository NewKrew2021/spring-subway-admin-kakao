package subway.query;

public class LineQuery {
    public static final String INSERT = "insert into line (name, color) values (?,?)";
    public static final String SELECT_ALL = "select id, name, color from line";
    public static final String SELECT_BY_ID = "select id, name, color from line where id = ?";
    public static final String DELETE_BY_ID = "delete from line where id = ?";
    public static final String UPDATE_BY_ID = "update line set name = ?, color = ? where id = ?";
    public static final String COUNT_BY_NAME = "select count(id) from line where name = ?";
}
