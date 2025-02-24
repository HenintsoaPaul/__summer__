package src.summer.exception.form;

public class MinParamException extends SummerFormException {

    double fieldValue, minValue;

    public MinParamException( String fieldName, double fieldValue, double minValue ) {
        super( fieldName );
        this.fieldValue = fieldValue;
        this.minValue = minValue;
    }

    @Override
    public String getCustomMessage() {
        return "Field \"" + fieldName + "\" (" + fieldValue + ") is under min value (" + minValue + ").";
    }

    @Override
    public String getMessage() {
        return this.getCustomMessage();
    }
}
