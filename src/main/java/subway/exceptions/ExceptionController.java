package subway.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exceptions.exception.*;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity lineNotFoundHandle() {
        return ResponseEntity.badRequest().body("해당 노선을 찾을 수 없습니다");
    }

    @ExceptionHandler(LineDuplicatedException.class)
    public ResponseEntity lineDuplicatedHandle() {
        return ResponseEntity.badRequest().body("중복된 노선입니다");
    }

    @ExceptionHandler(LineNothingToUpdateException.class)
    public ResponseEntity lineNothingToUpdateHandle() {
        return ResponseEntity.badRequest().body("업데이트가 가능한 노선이 존재하지 않습니다.");
    }

    @ExceptionHandler(SectionNoStationException.class)
    public ResponseEntity SectionNoStationHandle() {
        return ResponseEntity.badRequest().body("상행역과 하행역이 모두 노선에 포함되어 있습니다.");
        //ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(SectionSameSectionException.class)
    public ResponseEntity SectionSameStationHandle() {
        return ResponseEntity.badRequest().body("상행역과 하행역이 모두 노선에 포함되어 있습니다.");
    }

    @ExceptionHandler(SectionDeleteException.class)
    public ResponseEntity SectionDeleteHandle() {
        return ResponseEntity.badRequest().body("노선에 역이 존재하지 않거나, 노선에 역이 현재 2개뿐이 없습니다.");
    }

    @ExceptionHandler(SectionIllegalDistanceException.class)
    public ResponseEntity SectionIllegalDistanceHandle() {
        return ResponseEntity.badRequest().body("입력된 거리가 올바르지 않습니다.");
    }

}
