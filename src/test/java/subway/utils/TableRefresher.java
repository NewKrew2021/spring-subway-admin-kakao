package subway.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import subway.query.Sql;

public class TableRefresher {
    public static void refreshTables(JdbcTemplate jdbcTemplate) {
        refreshStation(jdbcTemplate);
        refreshLine(jdbcTemplate);
        refreshSection(jdbcTemplate);
    }

    public static void refreshStation(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(Sql.DROP_STATION_TABLE);
        jdbcTemplate.execute(Sql.CREATE_STATION_TABLE);
    }

    public static void refreshLine(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(Sql.DROP_LINE_TABLE);
        jdbcTemplate.execute(Sql.CREATE_LINE_TABLE);
    }

    public static void refreshSection(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(Sql.DROP_SECTION_TABLE);
        jdbcTemplate.execute(Sql.CREATE_SECTION_TABLE);
    }
}
