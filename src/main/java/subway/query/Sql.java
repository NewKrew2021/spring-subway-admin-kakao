package subway.query;

public class Sql {
    // 테이블 제거 관련
    public static final String DROP_STATION_TABLE = "drop table station if exists cascade";
    public static final String DROP_LINE_TABLE = "drop table line if exists cascade ";
    public static final String DROP_SECTION_TABLE = "drop table section if exists cascade";

    // 테이블 생성 관련
    public static final String CREATE_STATION_TABLE = "create table if not exists STATION\n" +
            "(\n" +
            "    id   bigint auto_increment not null,\n" +
            "    name varchar(255)          not null unique,\n" +
            "    primary key (id)\n" +
            ");";
    public static final String CREATE_LINE_TABLE = "create table if not exists LINE\n" +
            "(\n" +
            "    id    bigint auto_increment not null,\n" +
            "    name  varchar(255)          not null unique,\n" +
            "    color varchar(20)           not null,\n" +
            "    primary key (id)\n" +
            ");";
    public static final String CREATE_SECTION_TABLE = "create table if not exists SECTION\n" +
            "(\n" +
            "    id              bigint auto_increment not null,\n" +
            "    line_id         bigint                not null references LINE (id),\n" +
            "    up_station_id   bigint                not null references STATION (id),\n" +
            "    down_station_id bigint                not null references STATION (id),\n" +
            "    distance        int check (distance >= 1 and distance <= 100000),\n" +
            "    primary key (id)\n" +
            ");";

    // Station 관련
    public static final String INSERT_STATION = "insert into station (name) values (?)";
    public static final String SELECT_STATION_WITH_NAME = "select * from station where name = ?";
    public static final String SELECT_STATION_WITH_ID = "select * from station where id = ?";
    public static final String BATCH_SELECT_FROM_STATION = "select * from station where id in (:ids)";
    public static final String SELECT_ALL_STATIONS = "select * from station";
    public static final String DELETE_STATION_WITH_ID = "delete from station where id = ?";

    // Line 관련
    public static final String INSERT_LINE = "insert into line (name, color) values (?, ?)";
    public static final String SELECT_LINE_WITH_NAME = "select * from line where name = ?";
    public static final String SELECT_ALL_LINES = "select * from line";
    public static final String SELECT_LINE_WITH_ID = "select * from line where id = ?";
    public static final String UPDATE_LINE_WITH_ID = "update line set name = ?, color = ? where id = ?";
    public static final String DELETE_LINE_BY_ID = "delete from line where id = ?";

    // Section 관련
    public static final String SELECT_SECTION_WITH_LINE_ID = "select * from section where line_id = ?";
    public static final String DELETE_SECTION_WITH_ID = "delete from section where id = ?";
    public static final String DELETE_ALL_SECTION_WITH_LINE_ID = "delete from section where line_id = ?";
}