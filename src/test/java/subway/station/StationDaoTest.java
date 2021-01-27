package subway.station;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
class StationDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Test
    void checkExistNameTest() {
        String existSql = "select exists(select * from station where name=?) as success";
        assertThat(jdbcTemplate.queryForObject(existSql, Boolean.class, "잠실역")).isFalse();

        String insertSql = "insert into station (name) values (?)";
        jdbcTemplate.update(insertSql, "잠실역");
        assertThat(jdbcTemplate.queryForObject(existSql, Boolean.class, "잠실역")).isTrue();
    }
}