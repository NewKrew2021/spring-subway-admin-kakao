package subway.query;

public class SectionQuery {
    public static final String INSERT = "insert into section (line_id, up_station_id, down_station_id, distance, point_type) values (?,?,?,?,?)";
    public static final String DELETE_BY_ID = "delete from section where id = ?";
    public static final String SELECT_BY_LINE =
            "select id, line_id, up_station_id, down_station_id, distance, point_type from section where line_id = ?";
    public static final String SELECT_BY_LINE_AND_DOWNSTATION =
            "select id, line_id, up_station_id, down_station_id, distance, point_type from section where line_id = ? and down_station_id = ?";
    public static final String SELECT_BY_LINE_AND_UPSTATION =
            "select id, line_id, up_station_id, down_station_id, distance, point_type from section where line_id = ? and up_station_id = ?";
    public static final String SELECT_BY_LINE_AND_POINT_TYPE =
            "select id, line_id, up_station_id, down_station_id, distance, point_type from section where line_id = ? and point_type = ?";
    public static final String COUNT_BY_LINE = "select count(id) from section where line_id = ?";
}
