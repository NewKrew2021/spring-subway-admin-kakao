package subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;
import subway.station.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LineDaoTest {

    private final LineDao lineDao;

    @Autowired
    public LineDaoTest(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Test
    @DisplayName("노선이 제대로 저장되는지 확인한다.")
    void save() {
        Line savedLine = lineDao.save(new Line("신분당선", "bg-red-600"));
        Line foundLine = lineDao.findOne(savedLine.getId());

        assertThat(savedLine).isEqualTo(foundLine);
    }

    @Test
    @DisplayName("중복된 이름으로 노선을 저장할 때 예외가 발생하는지 확인한다.")
    void saveDuplicate() {
        assertThatThrownBy(() -> {
            lineDao.save(new Line("신분당선", "bg-red-600"));
            lineDao.save(new Line("신분당선", "bg-red-700"));
        }).isInstanceOf(DuplicateNameException.class);
    }

    @Test
    @DisplayName("목록을 조회했을 때 개수가 정확한지 확인한다.")
    void findAll() {
        lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.save(new Line("분당선", "bg-red-700"));
        lineDao.save(new Line("2호선", "bg-red-800"));

        assertThat(lineDao.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("노선이 제대로 수정되는지 확인한다.")
    void update() {
        Line savedLine = lineDao.save(new Line("신분당선", "bg-red-600"));
        Line updatedLine = new Line(savedLine.getId(), "분당선", "bg-red-700");
        lineDao.update(updatedLine);

        Line foundLine = lineDao.findOne(savedLine.getId());

        assertThat(updatedLine).isEqualTo(foundLine);
    }

    @Test
    @DisplayName("id 값이 없는 노선을 변경할 때 예외가 발생하는지 확인한다.")
    void updateNonExistence() {
        assertThatThrownBy(() -> {
            Line savedLine = lineDao.save(new Line("신분당선", "bg-red-600"));
            Line updatedLine = new Line(savedLine.getId() + 1, "분당선", "bg-red-700");
            lineDao.update(updatedLine);
        }).isInstanceOf(NoContentException.class);
    }


    @Test
    @DisplayName("삭제했을 때 더 이상 조회되지 않는 것을 확인한다.")
    void deleteById() {
        assertThatThrownBy(() -> {
            Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
            lineDao.deleteById(line.getId());
            lineDao.findOne(line.getId());
        }).isInstanceOf(NoContentException.class);
    }
}