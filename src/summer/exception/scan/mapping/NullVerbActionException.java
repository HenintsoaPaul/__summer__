package src.summer.exception.scan.mapping;

public class NullVerbActionException extends SummerMappingException {
    String message;

    public NullVerbActionException( String message ) {
        super();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
