package subway.section.dao;

public class SectionDaoQuery {
    public static final String INSERT = "insert into SECTION (line_id, station_id, distance) values(?, ?, ?)";
    public static final String DELETE = "delete from SECTION where line_id = ? and station_id = ?";
    public static final String FIND_BY_LINE_ID = "select * from SECTION where line_id = ?";
    public static final String COUNT_BY_LINE_ID = "select count(*) from SECTION where line_id = ?";
}
