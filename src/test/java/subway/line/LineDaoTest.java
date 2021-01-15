package subway.line;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class LineDaoTest {
    @Autowired LineDao lineDao;

    @Test
    public void insertTest(){
        lineDao.save(new Line("분당선", "노랑"));
        assertThatThrownBy(() ->
                lineDao.save( new Line("분당선", "빨강"))).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void updateTest(){
        lineDao.save(new Line("분당선", "노랑"));
        lineDao.save(new Line("신분당선", "초록"));
        assertThatThrownBy(() ->
                lineDao.update(2L, new Line("분당선", "노랑"))).isInstanceOf(DataAccessException.class);
    }
}
