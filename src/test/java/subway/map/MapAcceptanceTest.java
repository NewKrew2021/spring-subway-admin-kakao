package subway.map;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.AcceptanceTest;
import subway.line.LineResponse;
import subway.station.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static subway.station.StationAcceptanceTest.지하철역_등록되어_있음;

public class MapAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 역삼역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역");
        양재역 = 지하철역_등록되어_있음("양재역");
        역삼역 = 지하철역_등록되어_있음("역삼역");

        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 10);
        이호선 = 지하철_노선_등록되어_있음("2호선", "bg-red-600", 강남역, 역삼역, 10);
    }

    @Test
    void retrieveMap() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/maps")
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        MapResponse map = response.as(MapResponse.class);
        assertThat(map.getStations()).hasSize(3);
        assertThat(map.getLines()).hasSize(2);
    }
}
