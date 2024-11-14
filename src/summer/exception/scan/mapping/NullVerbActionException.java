package src.summer.exception.scan.mapping;

public class NullVerbActionException extends SummerMappingException {
    String message;

    public NullVerbActionException( String message ) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
