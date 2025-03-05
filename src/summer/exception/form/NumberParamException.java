package src.summer.exception.form;

public class NumberParamException extends SummerFormException {

    double fieldValue;

    public NumberParamException(String fieldName ) {
        super( fieldName );
    }

    @Override
    public String getCustomMessage() {
        return "Field \"" + fieldName + "\" must be a Number.";
    }

    @Override
    public String getMessage() {
        return this.getCustomMessage();
    }
}
