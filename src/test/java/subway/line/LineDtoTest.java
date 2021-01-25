package subway.line;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.LineDto;
import subway.http.request.LineRequest;

@DisplayName("LineDto 클래스")
public class LineDtoTest {

    @DisplayName("객체 생성 테스트")
    @Test
    public void create(){
        Assertions.assertThatCode(() -> {
            LineRequest req = new LineRequest("신분당선", "red", 1L, 2L, 100);
            LineDto dto = new LineDto(req);
        }).doesNotThrowAnyException();
    }
}
