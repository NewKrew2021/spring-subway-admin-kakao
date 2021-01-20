package subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.line.Line;
import subway.exception.NotExistException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LineServiceTest {

    @Autowired
    LineService lineService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE line IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE line(\n" +
                "    id bigint auto_increment not null,\n" +
                "    name varchar(255) not null unique,\n" +
                "    color varchar(20) not null,\n" +
                "    primary key(id)\n" +
                "    )");
    }

    @Test
    @DisplayName("노선을 생성하고 해당 노선에 대한 조회를 시도한다.")
    public void createAndGetLine() {
        Line newLine = lineService.createLine("2호선", "노란색");
        assertThat(lineService.getLine(newLine.getId()).getName()).isEqualTo("2호선");
        assertThat(lineService.getLine(newLine.getId()).getColor()).isEqualTo("노란색");
    }

    @Test
    @DisplayName("db내의 모든 노선들을 불러온다.")
    public void getAllLines() {
        lineService.createLine("2호선", "노란색");
        lineService.createLine("1호선", "노란색");
        lineService.createLine("3호선", "노란색");
        lineService.createLine("5호선", "노란색");
        assertThat(lineService.getAllLines().size()).isEqualTo(4);

    }

    @Test
    @DisplayName("이미 존재하는 노선의 정보를 변경한다.")
    public void updateLine() {
        //given
        Line newLine = lineService.createLine("2호선", "노란색");

        //when
        lineService.updateLine(newLine.getId(),"신분당선", "빨간색");

        //then
        assertThat(lineService.getLine(newLine.getId()).getColor()).isEqualTo("빨간색");
        assertThat(lineService.getLine(newLine.getId()).getName()).isEqualTo("신분당선");
    }

    @Test
    @DisplayName("존재하는 노선을 삭제하고 이를 조회했을 때 익셉션이 발생하는가")
    public void deleteLine() {
        lineService.createLine("1호선","파랑색");
        Line target = lineService.createLine("2호선","녹색");

        //when
        lineService.deleteLine(target.getId());

        //then
        assertThatThrownBy(()-> lineService.getLine(target.getId()))
                .isInstanceOf(NotExistException.class)
                .hasMessage(LineService.NOT_EXIST_LINE_ERROR_MESSAGE);
    }
}
