package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.dao.LineDao;
import subway.domain.line.Line;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LineDaoTest {

    @Autowired
    private LineDao lineDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE line IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE line(\n" +
                "    id bigint auto_increment not null,\n" +
                "    name varchar(255) not null unique,\n" +
                "    color varchar(20) not null,\n" +
                "    primary key(id)\n" +
                "    )");
    }

    @Test
    @DisplayName("id가 일치하는 노선을 반환한다.")
    public void findById_ifExist() {
        Line newLine = lineDao.save(new Line("신분당선", "빨간색"));
        assertThat(lineDao.findById(newLine.getId()).getName()).isEqualTo("신분당선");
    }

    @Test
    @DisplayName("id가 일치하는 노선이 존재하지 않으면 null을 반환한다.")
    public void findById_ifNotExist() {
        assertThat(lineDao.findById(3L)).isNull();
    }

    @Test
    @DisplayName("노선 정보를 업데이트한다.")
    public void updateById() {
        //when
        Line newLine = lineDao.save(new Line("신분당선", "빨간색"));
        lineDao.updateById(newLine.getId(),new Line("구분당선", "파란색"));

        //then
        assertThat(lineDao.findById(newLine.getId()).getName()).isEqualTo("구분당선");
        assertThat(lineDao.findById(newLine.getId()).getColor()).isEqualTo("파란색");
    }

    @Test
    @DisplayName("id값으로 해당 노선을 삭제한다.")
    public void deleteById() {
        Line target = lineDao.save(new Line("신분당선", "빨간색"));
        assertThat(lineDao.findById(target.getId()).getName()).isEqualTo("신분당선");
        lineDao.deleteById(target.getId());
        assertThat(lineDao.findById(target.getId())).isNull();
    }

    @Test
    @DisplayName("모든 노선을 찾는다.")
    public void findAll() {
        lineDao.save(new Line("1호선", "빨간색"));
        lineDao.save(new Line("2호선", "빨간색"));
        lineDao.save(new Line("3호선", "빨간색"));
        assertThat(lineDao.findAll().size()).isEqualTo(3);
    }


}
