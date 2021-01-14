package subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.NotExistException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class LineDaoTest {

    @Test
    @DisplayName("id가 일치하는 노선을 반환한다.")
    public void findById_ifExist() {
        LineDao lineDao = new LineDao();
        lineDao.save(new Line(1L, "신분당선", "빨간색",1L));
        assertThat(lineDao.findById(1L).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("id가 일치하는 노선이 존재하지 않으면 예외 발생.")
    public void findById_ifNotExist() {
        LineDao lineDao = new LineDao();
        lineDao.save(new Line(1L, "신분당선", "빨간색",1L));
        assertThatExceptionOfType(NotExistException.class).isThrownBy(() -> {
            lineDao.findById(3L).getId();
        }).withMessageMatching("해당 노선이 존재하지 않습니다.");
    }

    @Test
    public void updateById() {
        LineDao lineDao = new LineDao();
        lineDao.save(new Line(1L, "신분당선", "빨간색",1L));
        lineDao.updateById(1L, new Line("구분당선", "파란색", 1L));
        Line actual = lineDao.findById(1L);
        assertThat(actual.getName()).isEqualTo("구분당선");
        assertThat(actual.getColor()).isEqualTo("파란색");
    }
}
