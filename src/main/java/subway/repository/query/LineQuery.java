package subway.repository.query;

public class LineQuery {
    public static final String insertQuery = "insert into line (name, color) values(?, ?)";
    public static final String updateQuery = "update line set name = ?, color = ? where id = ?";
    public static final String selectByIdQuery = "select id, name, color from line where id = ?";
    public static final String selectAllQuery = "select id, name, color from line";
    public static final String deleteByIdQuery = "delete from line where id = ?";
    public static final String countByNameQuery = "select count(*) from line where name = ?";
}
