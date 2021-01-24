package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import subway.domain.section.Section;
import subway.domain.station.Station;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@SpringBootTest
public class SectionDaoTest {
    private static final Station 강남역 = new Station(1L);
    private static final Station 역삼역 = new Station(2L);
    private static final Station 광교역 = new Station(3L);

    private static final Section 강남_역삼 = new Section(1L, 강남역, 역삼역, 3);
    private static final Section 역삼_광교 = new Section(1L, 역삼역, 광교역, 4);
    private static final Section 역삼_광교_2 = new Section(2L, 역삼역, 광교역, 5);

    @Autowired
    SectionDao sectionDao;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void dropTable() {
        jdbcTemplate.execute("DROP TABLE section IF EXISTS");
        jdbcTemplate.execute("create table if not exists SECTION\n" +
                "(\n" +
                "    id bigint auto_increment not null,\n" +
                "    line_id bigint not null,\n" +
                "    up_station_id bigint not null,\n" +
                "    down_station_id bigint not null,\n" +
                "    distance int,\n" +
                "    primary key(id)\n" +
                ");");
    }

    @DisplayName("section을 저장한다.")
    @ParameterizedTest
    @MethodSource("provideLineForTest")
    public void save(Section section) {
        Section newSection = sectionDao.save(section);
        assertThat(newSection).isEqualTo(section);
    }

    @DisplayName("LineId로 section들을 조회한다.")
    @ParameterizedTest
    @MethodSource("provideLineForTest")
    public void getByLineId(Section section) {
        sectionDao.save(section);
        assertThat(sectionDao.getByLineId(section.getLineId())).contains(section);
    }

    @DisplayName("Seciton Id로 섹션을 삭제한다.")
    @ParameterizedTest
    @MethodSource("provideLineForTest")
    public void deleteById(Section section) {
        Section newSection = sectionDao.save(section);
        assertThat(sectionDao.deleteById(newSection.getId())).isTrue();
    }

    @DisplayName("존재하지 않는 ID로 섹션을 삭제한다.")
    @ParameterizedTest
    @MethodSource("provideLineForTest")
    public void failToDeleteById(Section section) {
        Section newSection = sectionDao.save(section);
        sectionDao.deleteById(newSection.getId());
        assertThat(sectionDao.deleteById(newSection.getId())).isFalse();
    }

    private static Stream<Arguments> provideLineForTest() {
        return Stream.of(
                Arguments.of(강남_역삼),
                Arguments.of(역삼_광교),
                Arguments.of(역삼_광교_2)
        );
    }
}
