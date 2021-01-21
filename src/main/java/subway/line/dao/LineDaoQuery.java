package subway.line.dao;

public class LineDaoQuery {
    public static final String INSERT = "insert into LINE (name, color) values(?, ?)";
    public static final String UPDATE = "update LINE set name = ?, color = ? where id = ?";
    public static final String DELETE = "delete from LINE where id = ?";
    public static final String FIND_ALL = "select * from LINE";
    public static final String FIND_BY_ID = "select * from LINE where id = ?";
    public static final String COUNT_BY_NAME = "select count(*) from LINE where name = ?";
}
