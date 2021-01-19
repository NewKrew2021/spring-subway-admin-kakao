package subway.query;

public class StationQuery {
    public static final String insertQuery = "insert into station (name) values(?)";
    public static final String selectAllQuery = "select id, name from station";
    public static final String selectByIdQuery = "select id, name from station where id = ?";
    public static final String deleteByIdQuery = "delete from station where id = ?";
    public static final String countByNameQuery = "select count(*) from station where name = ?";
}
