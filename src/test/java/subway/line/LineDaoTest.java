//package subway.line;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//public class LineDaoTest {
//
//    @Autowired
//    private LineDao lineDao;
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @BeforeEach
//    void setUp() {
//        jdbcTemplate.execute("DROP TABLE line IF EXISTS");
//        jdbcTemplate.execute("CREATE TABLE line(\n" +
//                "    id bigint auto_increment not null,\n" +
//                "    name varchar(255) not null unique,\n" +
//                "    color varchar(20) not null,\n" +
//                "    start_station_id bigint not null,\n" +
//                "    end_station_id bigint not null,\n" +
//                "    primary key(id)\n" +
//                "    )");
//    }
//
//    @Test
//    @DisplayName("id가 일치하는 노선을 반환한다.")
//    public void findById_ifExist() {
//        lineDao.save(new Line(1L, "신분당선", "빨간색",1L, 2L));
//        assertThat(lineDao.findById(1L).getId()).isEqualTo(1L);
//    }
//
//    @Test
//    @DisplayName("id가 일치하는 노선이 존재하지 않으면 null을 반환한다.")
//    public void findById_ifNotExist() {
//        lineDao.save(new Line(1L, "신분당선", "빨간색",1L, 2L));
//        assertThat(lineDao.findById(3L)).isNull();
//    }
//
//    @Test
//    public void updateById() {
//        lineDao.save(new Line(1L, "신분당선", "빨간색",1L, 2L));
//        lineDao.updateById(1L, new Line("구분당선", "파란색", 1L, 2L));
//        Line actual = lineDao.findById(1L);
//        assertThat(actual.getName()).isEqualTo("구분당선");
//        assertThat(actual.getColor()).isEqualTo("파란색");
//    }
//}
