package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.Section;
import subway.utils.TableRefresher;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 데이터 엑세스 관련 기능")
@SpringBootTest
public class SectionDaoTest {
    private final Section 섹션1 = new Section(1L, 1L, 2L, 3);
    private final Section 섹션2 = new Section(1L, 2L, 3L, 4);
    private final Section 섹션3 = new Section(2L, 2L, 3L, 5);

    @Autowired
    SectionDao sectionDao;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void refreshSection() {
        TableRefresher.refreshSection(jdbcTemplate);
    }

    @DisplayName("데이터베이스에 지하철 구간을 생성한다.")
    @Test
    public void save() {
        assertThat(sectionDao.save(섹션1)).isEqualTo(섹션1);
    }

    @DisplayName("데이터베이스의 지하철 구간 목록을 노선 아이디 기준으로 조회한다.")
    @Test
    public void getByLineId() {
        sectionDao.save(섹션1);
        sectionDao.save(섹션2);
        sectionDao.save(섹션3);
        assertThat(sectionDao.getByLineId(1L)).containsExactlyElementsOf(Arrays.asList(섹션1, 섹션2));
        assertThat(sectionDao.getByLineId(2L)).containsExactlyElementsOf(Arrays.asList(섹션3));
        assertThat(sectionDao.getByLineId(3L)).containsExactlyElementsOf(Collections.emptyList());
    }

    @DisplayName("데이터베이스의 지하철 구간을 제거한다.")
    @Test
    public void deleteById() {
        sectionDao.save(섹션1);
        assertThat(sectionDao.deleteById(1L)).isTrue();
        assertThat(sectionDao.deleteById(1L)).isFalse();
    }
}
