package subway.station.dao;

public class StationDaoQuery {
    public static final String INSERT = "insert into STATION (name) values(?)";
    public static final String FIND_ALL = "select * from STATION";
    public static final String FIND_BY_ID = "select id, name from STATION where id = ?";
    public static final String DELETE = "delete from STATION where id = ?";
    public static final String COUNT_BY_NAME = "select count(*) from STATION where name = ?";
}
