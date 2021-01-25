package subway.line;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class LineTest {

    @DisplayName("line객체 생성")
    @Test
    public void create(){
        Assertions.assertThatCode(() ->  {
            new Line(1L, "신분당선", "red");
        }).doesNotThrowAnyException();
    }

    @DisplayName("LineDto로 line객체 생성")
    @Test
    public void createByDto(){
        //given
        LineRequest req = new LineRequest("신분당선", "red", 1L, 2L, 100);
        LineDto dto = new LineDto(req);

        //when, then
        Assertions.assertThatCode(() -> {
            new Line(dto);
            new Line(1L, dto);
        }).doesNotThrowAnyException();
    }
}
