package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import subway.domain.line.Line;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("provideLine")
    public void saveTest(String name, String color) {
        Line line = new Line(name, color);
        Line savedLine = lineDao.save(line);

        assertThat(savedLine).isEqualTo(line);
    }

    @ParameterizedTest
    @MethodSource("provideLine")
    public void saveDuplicatedNameTest(String name, String color) {
        Line line = new Line(name, color);
        lineDao.save(line);

        assertThatThrownBy(() ->
                lineDao.save(new Line(name, color))).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void updateTest() {
        lineDao.save(분당선);
        lineDao.save(신분당선);

        lineDao.update(2L, 수인선);

        assertThat(lineDao.getById(2L)).isEqualTo(수인선);
    }

    @Test
    public void failToUpdateTest() {
        lineDao.save(분당선);
        lineDao.save(신분당선);

        assertThatThrownBy(() ->
                lineDao.update(2L, 분당선)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void findAllTest() {
        lineDao.save(분당선);
        lineDao.save(신분당선);

        List<Line> lines = lineDao.findAll();

        assertThat(lines).containsExactlyElementsOf(Arrays.asList(분당선, 신분당선));
    }

    @ParameterizedTest
    @MethodSource("provideLine")
    public void getByIdTest(String name, String color) {
        Line line = lineDao.save(new Line(name, color));

        assertThat(lineDao.getById(line.getId())).isEqualTo(line);
    }

    @ParameterizedTest
    @MethodSource("provideLine")
    public void deleteByIdTest(String name, String color) {
        Line line = lineDao.save(new Line(name, color));

        assertThat(lineDao.deleteById(line.getId())).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLine")
    public void failToDeleteByIdTest(String name, String color) {
        Line line = lineDao.save(new Line(name, color));

        lineDao.deleteById(line.getId());

        assertThat(lineDao.deleteById(line.getId())).isFalse();
    }

    private static Stream<Arguments> provideLine() {
        return Stream.of(
                Arguments.of("분당선", "빨강"),
                Arguments.of("신분당선", "초록"),
                Arguments.of("수인선", "파랑")
        );
    }
}
