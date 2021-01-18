package subway.query;

public class Sql {
    // Station 관련
    public static final String SELECT_STATION_WITH_ID = "select * from station where id = ?";
    public static final String BATCH_SELECT_FROM_STATION = "select * from station where id in (:ids)";
    public static final String SELECT_ALL_STATIONS = "select * from station";
    public static final String DELETE_STATION_WITH_ID = "delete from station where id = ?";

    // Line 관련
    public static final String SELECT_ALL_LINES = "select * from line";
    public static final String SELECT_LINE_WITH_ID = "select * from line where id = ?";
    public static final String UPDATE_LINE_WITH_ID = "update line set name = ?, color = ? where id = ?";
    public static final String DELETE_LINE_BY_ID = "delete from line where id = ?";

    // Section 관련
    public static final String SELECT_SECTION_WITH_LINE_ID = "select * from section where line_id = ?";
    public static final String DELETE_SECTION_WITH_ID = "delete from section where id = ?";
    public static final String DELETE_ALL_SECTION_WITH_LINE_ID = "delete from section where line_id = ?";
}
