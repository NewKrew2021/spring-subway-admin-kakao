package subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DirtiesContext
@SpringBootTest
public class LineDaoTest {
    private final Line 분당선 = new Line("분당선", "노랑");
    private final Line 신분당선 = new Line("신분당선", "초록");
    private final Line 수인선 = new Line("수인선", "파랑");
    @Autowired
    LineDao lineDao;
    @Autowired
    JdbcTemplate jdbcTemplate;

//    @AfterEach
    @BeforeEach
    public void dropTable() {
        jdbcTemplate.execute("DROP TABLE line IF EXISTS");
        jdbcTemplate.execute("create table if not exists LINE\n" +
                "(\n" +
                "    id bigint auto_increment not null,\n" +
                "    name varchar(255) not null unique,\n" +
                "    color varchar(20) not null,\n" +
                "    primary key(id)\n" +
                ");");
    }

    @Test
    public void saveTest() {
        assertThat(lineDao.save(분당선).getName()).isEqualTo(분당선.getName());
        assertThatThrownBy(() ->
                lineDao.save(new Line("분당선", "빨강"))).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void updateTest() {
        lineDao.save(분당선);
        lineDao.save(신분당선);
        lineDao.update(2L, 수인선);
        assertThat(lineDao.getById(2L)).isEqualTo(수인선);
        assertThatThrownBy(() ->
                lineDao.update(2L, 분당선)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void findAllTest() {
        lineDao.save(분당선);
        lineDao.save(신분당선);
        assertThat(lineDao.findAll()).containsExactlyElementsOf(Arrays.asList(분당선, 신분당선));
    }

    @Test
    public void getByIdTest() {
        lineDao.save(분당선);
        assertThat(lineDao.getById(1L)).isEqualTo(분당선);
    }

    @Test
    public void deleteByIdTest() {
        lineDao.save(분당선);
        assertThat(lineDao.deleteById(1L)).isTrue();
        assertThat(lineDao.deleteById(1L)).isFalse();
    }
}
