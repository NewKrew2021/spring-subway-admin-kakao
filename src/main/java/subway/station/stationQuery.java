package subway.station;

public class stationQuery {
    public static final String insert = "insert into station(name) values (?)";
    public static final String selectIdAndNameByName = "select id, name from station where name = ?";
    public static final String selectIdAndNameById = "select id, name from station where id = ?";
    public static final String selectIdAndNameOfAll = "select id, name from station";
    public static final String deleteById = "delete from station where id = ?";
}
