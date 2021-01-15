package subway.line;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import subway.station.Station;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineServiceTest {

    private static LineService lineService;

    @BeforeAll
    static void setUp(){
        lineService = new LineService();
    }

    @Test
    public void createLine() {
        LineRequest lineRequest = new LineRequest("test","yellow",1L,2L,10);
        List<Station> stations = Arrays.asList(new Station(1L,"a"), new Station(2L,"v"));
        assertThat(lineService.createLine(lineRequest))
                .isEqualTo(new LineResponse(1L,"test","yellow",stations));
    }
}
