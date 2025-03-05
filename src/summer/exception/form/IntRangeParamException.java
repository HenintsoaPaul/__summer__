package src.summer.exception.form;

public class IntRangeParamException extends SummerFormException {
    int fieldValue, minValue, maxValue;

    public IntRangeParamException(String fieldName, int fieldValue, int minValue, int maxValue) {
        super(fieldName);
        this.fieldValue = fieldValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public String getCustomMessage() {
        return "Field \"" + fieldName + "\" (" + fieldValue + ") is outside range value (" + minValue + ";" + maxValue + ").";
    }

    @Override
    public String getMessage() {
        return this.getCustomMessage();
    }
}
