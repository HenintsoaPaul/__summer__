package src.summer.exception.form;

public class RangeParamException extends SummerFormException {

    double fieldValue, minValue, maxValue;

    public RangeParamException(String fieldName, double fieldValue, double minValue, double maxValue) {
        super(fieldName);
        this.fieldValue = fieldValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public String getCustomMessage() {
        return "Field \"" + fieldName + "\" (" + fieldValue + ") is outside range value [" + minValue + ";" + maxValue + "].";
    }

    @Override
    public String getMessage() {
        return this.getCustomMessage();
    }
}
