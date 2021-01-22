package subway.section;

public class SectionQuery {
    public static final String insert = "insert into section (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";
    public static final String update = "update section set line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
    public static final String deleteById = "delete from section where id = ?";
    public static final String selectByLineId = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = ?";
}
