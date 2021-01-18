package subway.station;

class StationQuery {
    public static final String insertQuery = "insert into station (name) values(?)";
    public static final String selectAllQuery = "select * from station";
    public static final String selectByIdQuery = "select * from station where id = ?";
    public static final String deleteByIdQuery = "delete from station where id = ?";
    public static final String countByNameQuery = "select count(*) from station where name = ?";
}
