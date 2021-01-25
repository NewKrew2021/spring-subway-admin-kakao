package subway.line;

public class LineQuery {
    public static final String insert = "insert into line (name, color) values(?, ?)";
    public static final String selectIdNameColorByName = "select id, name, color from line where name = ?";
    public static final String selectIdNameColorById = "select id, name, color from line where id = ?";
    public static final String selectIdNameColorOfAll = "select id, name, color from line";
    public static final String updateNameAndColorById = "update line set name = ?, color = ? where id = ?";
    public static final String deleteById = "delete from line where id = ?";
    public static final String countByName = "select count(*) from line where name = ?";
}
