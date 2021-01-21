package subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.AcceptanceTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import subway.line.presentation.LineRequest;
import subway.station.presentation.StationResponse;
import subway.station.presentation.StationRequest;
import subway.station.domain.Station;
import static org.assertj.core.api.Assertions.assertThat;
import static subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private StationResponse 강북역;
    private StationResponse 판교역;

    private static final String 강남역 = "강남역";
    private static final String 역삼역 = "역삼역";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);

        // then
        지하철역_생성됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        StationResponse stationResponse1 = 지하철역_등록되어_있음(강남역);
        StationResponse stationResponse2 = 지하철역_등록되어_있음(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청();

        // then
        지하철역_목록_응답됨(response);
        지하철역_목록_포함됨(response, Arrays.asList(stationResponse1, stationResponse2));
    }

    @DisplayName("지하철역 중복을 확인한다.")
    @Test
    void duplicateStation(){
        지하철역_생성_요청(강남역);

        지하철역_중복_확인(강남역);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse stationResponse = 지하철역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_제거_요청(stationResponse);

        // then
        지하철역_삭제됨(response);

        // "강남역" 으로 보내고, 해당하는 데이터를 가져온다
        // body가 비어있는걸 확인한다
        assertThat(지하철역_삭제_확인(강남역)).isEqualTo(false);

    }

    @DisplayName("지하철역이 구간에 사용되고 있는 경우 삭제하지 못한다.")
    @Test
    void deleteStationException(){
        강북역 = 지하철역_등록되어_있음("강북역");
        판교역 = 지하철역_등록되어_있음("판교역");

        LineRequest lineRequest = new LineRequest("강북판교노선", "bg-red-600", 강북역.getId(), 판교역.getId(), 10);

        지하철_노선_등록되어_있음(lineRequest);

        ExtractableResponse<Response> response = 지하철역_제거_요청(강북역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역_중복_확인(String name){
        RestAssured
                .given().log().all()
                .body(new StationRequest(강남역))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    public static boolean 지하철역_삭제_확인(String name){
        return 지하철역_목록_조회_요청().jsonPath().getList(".", Station.class).stream()
                .filter(val -> val.getName().equals(name))
                .findAny()
                .isPresent();
    }

    public static StationResponse 지하철역_등록되어_있음(String name) {
        return 지하철역_생성_요청(name).as(StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        StationRequest stationRequest = new StationRequest(name);

        return RestAssured
                .given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_제거_요청(StationResponse stationResponse) {
        return RestAssured
                .given().log().all()
                .when().delete("/stations/" + stationResponse.getId())
                .then().log().all()
                .extract();
    }

    public static void 지하철역_생성됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철역_목록_포함됨(ExtractableResponse<Response> response, List<StationResponse> createdResponses) {
        List<Long> expectedLineIds = createdResponses.stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }
}
