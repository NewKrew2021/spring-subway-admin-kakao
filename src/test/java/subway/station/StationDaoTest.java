package subway.station;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.dao.StationDao;
import subway.domain.Station;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class StationDaoTest {
    @Autowired
    StationDao stationDao;

    Station 사당역 = new Station("사당역");

    @Test
    void testInsert() {
        Station newStation = stationDao.save(사당역);
        assertEquals(1L, newStation.getId());
        assertEquals("사당역", newStation.getName());
    }

    @Test
    void testFind() {
        Station newStation = stationDao.save(사당역);
        Station foundStation = stationDao.findById(newStation.getId()).get();
        assertEquals(newStation, foundStation);
    }

    @Test
    void testNotFound() {
        Station newStation = stationDao.save(사당역);
        assertEquals(Optional.empty(), stationDao.findById(newStation.getId() + 10));
    }

    @Test
    void testDelete() {
        Station newStation = stationDao.save(사당역);
        stationDao.deleteById(newStation.getId());
        assertEquals(Optional.empty(), stationDao.findById(newStation.getId()));
    }

}
