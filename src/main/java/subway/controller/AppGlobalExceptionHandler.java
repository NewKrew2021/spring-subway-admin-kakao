package subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.exception.*;

@RestControllerAdvice
public class AppGlobalExceptionHandler {

    public static final String ALREADY_EXIST_DATA_STRING = "이미 존재하는 데이터 입니다.";
    public static final String EMPTY_DATA_STRING = "존재하지 않는 데이터 입니다.";
    public static final String WRONG_DISTANCE_STRING = "잘못된 거리 입니다.";
    public static final String ILLEGAL_STATION_STRING = "잘못된 역 정보 입니다.";
    public static final String DELETE_IMPOSSIBLE_STRING = "삭제가 불가능 합니다.";
    public static final String UPDATE_IMPOSSIBLE_STRING = "수정이 불가능 합니다.";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyExistDataException.class)
    public String alreadyExistDataHandler() {
        return ALREADY_EXIST_DATA_STRING;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataEmptyException.class)
    public String dataEmptyHandler() {
        return EMPTY_DATA_STRING;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DistanceException.class)
    public String distanceHandler() {
        return WRONG_DISTANCE_STRING;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStationException.class)
    public String illegalStationHandler() {
        return ILLEGAL_STATION_STRING;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DeleteImpossibleException.class)
    public String deleteImpossibleHandler() {
        return DELETE_IMPOSSIBLE_STRING;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UpdateImpossibleException.class)
    public String updateImpossibleHandler() {
        return UPDATE_IMPOSSIBLE_STRING;
    }
}
