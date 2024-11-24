package src.summer.exception.form;

public class RequiredParamException extends SummerFormException {
    public RequiredParamException( String fieldName ) {
        super( fieldName );
    }

    @Override
    public String getCustomMessage() {
        return "Field \"" + this.fieldName + "\" is required.";
    }

    @Override
    public String getMessage() {
        return this.getCustomMessage();
    }
}
