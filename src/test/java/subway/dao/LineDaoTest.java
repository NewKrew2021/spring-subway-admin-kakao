package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Line;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("노선 데이터 엑세스 관련 기능")
@SpringBootTest
@Sql("classpath:/deleteAll.sql")
public class LineDaoTest {
    private final Line 분당선 = new Line("분당선", "노랑");
    private final Line 신분당선 = new Line("신분당선", "초록");
    private final Line 수인선 = new Line("수인선", "파랑");
    @Autowired
    private LineDao lineDao;

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
        Line 분당선_삽입됨 = lineDao.save(신분당선);
        lineDao.update(분당선_삽입됨.getId(), 수인선);
        assertThat(lineDao.getById(분당선_삽입됨.getId())).isEqualTo(수인선);
        assertThatThrownBy(() ->
                lineDao.update(분당선_삽입됨.getId(), 분당선)).isInstanceOf(DataAccessException.class);
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
        Line 분당선_삽입됨 = lineDao.save(분당선);
        assertThat(lineDao.getById(분당선_삽입됨.getId())).isEqualTo(분당선);
    }

    @DisplayName("데이터베이스의 지하철 노선을 삭제한다.")
    @Test
    public void deleteByIdTest() {
        Line 분당선_삽입됨 = lineDao.save(분당선);
        assertThat(lineDao.deleteById(분당선_삽입됨.getId())).isTrue();
        assertThat(lineDao.deleteById(분당선_삽입됨.getId())).isFalse();
    }
}
