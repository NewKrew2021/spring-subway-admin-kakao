package subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dao.UpdateImpossibleException;
import subway.domain.DistanceException;
import subway.domain.IllegalStationException;
import subway.exception.*;

@RestControllerAdvice
public class AppGlobalExceptionHandler {

    public static final String ALREADY_EXIST_DATA_STRING = "이미 존재하는 데이터 입니다.";
    public static final String EMPTY_DATA_STRING = "존재하지 않는 데이터 입니다.";
    public static final String WRONG_DISTANCE_STRING = "잘못된 거리 입니다.";
    public static final String ILLEGAL_STATION_STRING = "잘못된 역 정보 입니다.";
    public static final String DELETE_IMPOSSIBLE_STRING = "삭제가 불가능 합니다.";
    public static final String UPDATE_IMPOSSIBLE_STRING = "수정이 불가능 합니다.";

    @ExceptionHandler(AlreadyExistDataException.class)
    public ResponseEntity<String> alreadyExistDataHandler() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ALREADY_EXIST_DATA_STRING);
    }

    @ExceptionHandler(DataEmptyException.class)
    public ResponseEntity<String> dataEmptyHandler() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EMPTY_DATA_STRING);
    }

    @ExceptionHandler(DistanceException.class)
    public ResponseEntity<String> distanceHandler() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(WRONG_DISTANCE_STRING);
    }

    @ExceptionHandler(IllegalStationException.class)
    public ResponseEntity<String> illegalStationHandler() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ILLEGAL_STATION_STRING);
    }

    @ExceptionHandler(DeleteImpossibleException.class)
    public ResponseEntity<String> deleteImpossibleHandler() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DELETE_IMPOSSIBLE_STRING);
    }

    @ExceptionHandler(UpdateImpossibleException.class)
    public ResponseEntity<String> updateImpossibleHandler() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UPDATE_IMPOSSIBLE_STRING);
    }
}
