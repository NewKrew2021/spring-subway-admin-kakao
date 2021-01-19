package subway.station.query;

public class StationQuery {
    public static final String INSERT = "insert into station (name) values (?)";
    public static final String SELECT_ALL = "select id, name from station";
    public static final String SELECT_BY_ID = "select id, name from station where id = ?";
    public static final String DELETE_BY_ID = "delete from station where id = ?";
}
