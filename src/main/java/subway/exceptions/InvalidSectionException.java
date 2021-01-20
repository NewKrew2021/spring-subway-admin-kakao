package subway.exceptions;

public class InvalidSectionException extends RuntimeException{
    public InvalidSectionException(String errorMessage){
        super(errorMessage);
    }
}
