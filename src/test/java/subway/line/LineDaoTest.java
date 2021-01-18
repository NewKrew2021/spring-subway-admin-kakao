package subway.line;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class LineDaoTest {
    @Autowired
    LineDao lineDao;

    Line 이호선 = new Line("이호선", "그린");

    @Test
    void testInsert() {
        Line newLine = lineDao.save(이호선);
        assertEquals(1L, newLine.getId());
    }

    @Test
    void testFind() {
        Line newLine = lineDao.save(이호선);
        Line foundLine = lineDao.findById(newLine.getId()).get();
        assertEquals(newLine, foundLine);
    }

    @Test
    void testNotFound() {
        Line newLine = lineDao.save(이호선);
        assertEquals(Optional.empty(), lineDao.findById(newLine.getId() + 10));
    }

    @Test
    void testDelete() {
        Line newLine = lineDao.save(이호선);
        lineDao.deleteById(newLine.getId());
        assertEquals(Optional.empty(), lineDao.findById(newLine.getId()));
    }

    @Test
    void testUpdate() {
        Line newLine = lineDao.save(이호선);
        Line updateLine = new Line(newLine.getId(), newLine.getName(), "레드");
        lineDao.update(updateLine);

        Line foundLine = lineDao.findById(newLine.getId()).get();
        assertEquals("레드", foundLine.getColor());
    }
}
