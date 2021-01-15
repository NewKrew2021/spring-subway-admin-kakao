package subway.utils;

import org.springframework.jdbc.core.JdbcTemplate;

public class TableRefresher {
    public static void refreshTables(JdbcTemplate jdbcTemplate) {
        refreshStation(jdbcTemplate);
        refreshLine(jdbcTemplate);
        refreshSection(jdbcTemplate);
    }

    public static void refreshStation(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("DROP TABLE STATION IF EXISTS");
        jdbcTemplate.execute("create table if not exists STATION\n" +
                "(\n" +
                "    id bigint auto_increment not null,\n" +
                "    name varchar(255) not null unique,\n" +
                "    primary key(id)\n" +
                ");");
    }

    public static void refreshLine(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("DROP TABLE line IF EXISTS");
        jdbcTemplate.execute("create table if not exists LINE\n" +
                "(\n" +
                "    id bigint auto_increment not null,\n" +
                "    name varchar(255) not null unique,\n" +
                "    color varchar(20) not null,\n" +
                "    primary key(id)\n" +
                ");");
    }

    public static void refreshSection(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("DROP TABLE section IF EXISTS");
        jdbcTemplate.execute("create table if not exists SECTION\n" +
                "(\n" +
                "    id bigint auto_increment not null,\n" +
                "    line_id bigint not null,\n" +
                "    up_station_id bigint not null,\n" +
                "    down_station_id bigint not null,\n" +
                "    distance int,\n" +
                "    primary key(id)\n" +
                ");");
    }
}
