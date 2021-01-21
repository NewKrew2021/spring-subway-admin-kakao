package subway.station.dao;

public class StationDaoQuery {
    public static final String INSERT = "insert into station (name) values(?)";
    public static final String FIND_ALL = "select * from station";
    public static final String FIND_BY_ID = "select id, name from station where id = ?";
    public static final String DELETE = "delete from station where id = ?";
    public static final String COUNT_BY_NAME = "select count(*) from station where name = ?";
}
