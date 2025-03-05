package src.summer.exception.form;

public class IntParamException extends SummerFormException {
    int fieldValue;

    public IntParamException(String fieldName ) {
        super( fieldName );
    }

    @Override
    public String getCustomMessage() {
        return "Field \"" + fieldName + "\" must be an Integer.";
    }

    @Override
    public String getMessage() {
        return this.getCustomMessage();
    }
}
