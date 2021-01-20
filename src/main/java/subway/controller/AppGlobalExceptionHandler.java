package subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.exception.*;

@RestControllerAdvice
public class AppGlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyExistDataException.class)
    public String alreadyExistDataHandler() {
        return "이미 존재하는 데이터 입니다.";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataEmptyException.class)
    public String dataEmptyHandler() {
        return "존재하지 않는 데이터 입니다.";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DistanceException.class)
    public String distanceHandler() {
        return "잘못된 거리 입니다.";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStationException.class)
    public String illegalStationHandler() {
        return "잘못된 역 정보 입니다.";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DeleteImpossibleException.class)
    public String deleteImpossibleHandler() {
        return "삭제가 불가능 합니다.";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UpdateImpossibleException.class)
    public String updateImpossibleHandler() {
        return "수정이 불가능 합니다.";
    }
}
