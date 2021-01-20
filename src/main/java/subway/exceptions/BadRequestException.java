package subway.exceptions;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String errorMessage){
        super(errorMessage);
    }
}
