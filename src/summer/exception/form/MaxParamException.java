package src.summer.exception.form;

public class MaxParamException extends SummerFormException {
    int fieldValue, minValue;

    public MaxParamException(String fieldName, int fieldValue, int minValue ) {
        super( fieldName );
        this.fieldValue = fieldValue;
        this.minValue = minValue;
    }

    @Override
    public String getCustomMessage() {
        return "Field \"" + fieldName + "\" (" + fieldValue + ") is over max value (" + minValue + ").";
    }

    @Override
    public String getMessage() {
        return this.getCustomMessage();
    }
}
