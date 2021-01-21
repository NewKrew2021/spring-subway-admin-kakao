package subway.station;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Station 클래스")
public class StationTest {

    @DisplayName("객체 생성")
    @Test
    public void create(){
        Assertions.assertThatCode(() -> {
            new Station(1L, "미금역");
        }).doesNotThrowAnyException();
    }
}
