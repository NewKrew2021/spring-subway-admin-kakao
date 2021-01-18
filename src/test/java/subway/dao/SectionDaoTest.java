package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 데이터 엑세스 관련 기능")
@SpringBootTest
@Sql("classpath:/deleteAll.sql")
public class SectionDaoTest {
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineDao lineDao;

    private Line 분당선;
    private Line 중앙선;
    private Section 분당선_수서서현;
    private Section 분당선_서현수내;
    private Section 중앙선_서현수내;

    @BeforeEach
    public void refreshSection() {
        Station 수서역 = stationDao.save(new Station("수서역"));
        Station 서현역 = stationDao.save(new Station("서현역"));
        Station 수내역 = stationDao.save(new Station("수내역"));
        분당선 = lineDao.save(new Line("분당선", "노랑"));
        중앙선 = lineDao.save(new Line("신분당선", "빨강"));
        분당선_수서서현 = new Section(분당선.getId(), 수서역.getId(), 서현역.getId(), 3);
        분당선_서현수내 = new Section(분당선.getId(), 서현역.getId(), 수내역.getId(), 4);
        중앙선_서현수내 = new Section(중앙선.getId(), 서현역.getId(), 수내역.getId(), 4);
    }

    @DisplayName("데이터베이스에 지하철 구간을 생성한다.")
    @Test
    public void saveTest() {
        assertThat(sectionDao.save(분당선_수서서현)).isEqualTo(분당선_수서서현);
    }

    @DisplayName("데이터베이스의 지하철 구간 목록을 노선 아이디 기준으로 조회한다.")
    @Test
    public void getByLineIdTest() {
        sectionDao.save(분당선_수서서현);
        sectionDao.save(분당선_서현수내);
        sectionDao.save(중앙선_서현수내);
        assertThat(sectionDao.getByLineId(분당선.getId())).containsExactlyElementsOf(Arrays.asList(분당선_수서서현, 분당선_서현수내));
        assertThat(sectionDao.getByLineId(중앙선.getId())).containsExactlyElementsOf(Arrays.asList(중앙선_서현수내));
        assertThat(sectionDao.getByLineId(-1L)).containsExactlyElementsOf(Collections.emptyList());
    }

    @DisplayName("데이터베이스의 지하철 구간을 제거한다.")
    @Test
    public void deleteByIdTest() {
        Section 분당선_수내서현_삽입됨 = sectionDao.save(분당선_수서서현);
        assertThat(sectionDao.deleteById(분당선_수내서현_삽입됨.getId())).isTrue();
        assertThat(sectionDao.deleteById(분당선_수내서현_삽입됨.getId())).isFalse();
    }
}
