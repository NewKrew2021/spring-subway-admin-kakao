package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("지하철역 데이터 엑세스 관련 기능")
@SpringBootTest
@Sql("classpath:/deleteAll.sql")
public class StationDaoTest {
    private final Station 강남역 = new Station("강남역");
    private final Station 역삼역 = new Station("역삼역");

    @Autowired
    private StationDao stationDao;

    @DisplayName("데이터베이스의 지하철역을 생성한다.")
    @Test
    public void saveTest() {
        assertThat(stationDao.save(강남역)).isEqualTo(강남역);
        assertThatThrownBy(() -> stationDao.save(강남역)).isInstanceOf(DataAccessException.class);
    }

    @DisplayName("데이터베이스의 지하철역을 조회한다.")
    @Test
    public void getByIdTest() {
        Station 강남역_삽입됨 = stationDao.save(강남역);
        assertThat(stationDao.getById(강남역_삽입됨.getId())).isEqualTo(강남역);
    }

    @DisplayName("데이터베이스의 여러 지하철역을 조회한다.")
    @Test
    public void batchGetByIdTest() {
        Station 강남역_삽입됨 = stationDao.save(강남역);
        Station 역삼역_삽입됨 = stationDao.save(역삼역);
        assertThat(stationDao.batchGetByIds(Arrays.asList(강남역_삽입됨.getId(), 역삼역_삽입됨.getId())))
                .isEqualTo(Arrays.asList(강남역, 역삼역));
    }

    @DisplayName("데이터베이스의 지하철역 목록을 조회한다.")
    @Test
    public void findAllTest() {
        stationDao.save(강남역);
        stationDao.save(역삼역);
        assertThat(stationDao.findAll()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역));
    }

    @DisplayName("데이터베이스의 지하철역을 제거한다.")
    @Test
    public void deleteByIdTest() {
        Station 강남역_삽입됨 = stationDao.save(강남역);
        assertThat(stationDao.deleteById(강남역_삽입됨.getId())).isTrue();
        assertThat(stationDao.deleteById(강남역_삽입됨.getId())).isFalse();
    }
}
