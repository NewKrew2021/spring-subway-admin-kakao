package subway.section;

public class SectionQuery {
    public static final String insertQuery = "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
    public static final String selectByIdQuery = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = ?";
    public static final String deleteByIdQuery = "delete from section where id = ?";
}
