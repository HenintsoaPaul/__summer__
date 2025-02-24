package src.summer.exception.form;

public class MaxParamException extends SummerFormException {

    double fieldValue, maxValue;

    public MaxParamException(String fieldName, double fieldValue, double maxValue ) {
        super( fieldName );
        this.fieldValue = fieldValue;
        this.maxValue = maxValue;
    }

    @Override
    public String getCustomMessage() {
        return "Field \"" + fieldName + "\" (" + fieldValue + ") is over max value (" + maxValue + ").";
    }

    @Override
    public String getMessage() {
        return this.getCustomMessage();
    }
}
