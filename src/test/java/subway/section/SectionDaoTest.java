package subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.domain.Line;
import subway.dao.LineDao;
import subway.domain.Station;
import subway.dao.StationDao;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class SectionDaoTest {
    @Autowired
    SectionDao sectionDao;
    @Autowired
    LineDao lineDao;
    @Autowired
    StationDao stationDao;

    Line 이호선 = new Line(1L, "이호선", "그린");
    Station 사당역 = new Station(1L, "사당역");
    Station 방배역 = new Station(2L, "방배역");
    Station 서초역 = new Station(3L, "서초역");
    Station 교대역 = new Station(4L, "교대역");

    @BeforeEach
    void setUp(){
        lineDao.save(이호선);
        stationDao.save(사당역);
        stationDao.save(방배역);
        stationDao.save(서초역);
        stationDao.save(교대역);
    }

    @Test
    void testInsert() {
        Section newSection = sectionDao.save(new Section(이호선, 사당역, 방배역, 10));
        assertEquals(1L, newSection.getId());

        Section newSection2 = sectionDao.save(new Section(이호선, 사당역, 방배역, 10));
        assertEquals(2L, newSection2.getId());
    }

    @Test
    void testDelete() {
        Section newSection = sectionDao.save(new Section(이호선, 사당역, 방배역, 10));
        Section newSection2 = sectionDao.save(new Section(이호선, 사당역, 방배역, 10));

        sectionDao.deleteById(newSection2.getId());
        assertEquals(1, sectionDao.findAllByLine(이호선).getSize());
    }

}
