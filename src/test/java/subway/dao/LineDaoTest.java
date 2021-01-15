package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.Line;
import subway.utils.TableRefresher;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("노선 데이터 엑세스 관련 기능")
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
    public void refreshLine() {
        TableRefresher.refreshLine(jdbcTemplate);
    }

    @DisplayName("데이터베이스에 지하철 노선을 생성한다.")
    @Test
    public void saveTest() {
        assertThat(lineDao.save(분당선).getName()).isEqualTo(분당선.getName());
        assertThatThrownBy(() ->
                lineDao.save(new Line("분당선", "빨강"))).isInstanceOf(DataAccessException.class);
    }

    @DisplayName("데이터베이스의 지하철 노선을 수정한다.")
    @Test
    public void updateTest() {
        lineDao.save(분당선);
        lineDao.save(신분당선);
        lineDao.update(2L, 수인선);
        assertThat(lineDao.getById(2L)).isEqualTo(수인선);
        assertThatThrownBy(() ->
                lineDao.update(2L, 분당선)).isInstanceOf(DataAccessException.class);
    }

    @DisplayName("데이터베이스의 지하철 노선을 전부 조회한다.")
    @Test
    public void findAllTest() {
        lineDao.save(분당선);
        lineDao.save(신분당선);
        assertThat(lineDao.findAll()).containsExactlyElementsOf(Arrays.asList(분당선, 신분당선));
    }

    @DisplayName("데이터베이스의 지하철 노선을 조회한다.")
    @Test
    public void getByIdTest() {
        lineDao.save(분당선);
        assertThat(lineDao.getById(1L)).isEqualTo(분당선);
    }

    @DisplayName("데이터베이스의 지하철 노선을 삭제한다.")
    @Test
    public void deleteByIdTest() {
        lineDao.save(분당선);
        assertThat(lineDao.deleteById(1L)).isTrue();
        assertThat(lineDao.deleteById(1L)).isFalse();
    }
}
