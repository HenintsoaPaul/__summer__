package src.summer.beans.validation;

import src.summer.exception.form.SummerFormException;

import java.util.ArrayList;
import java.util.List;

public class ValidationLog {

    /**
     * Path to the fallback view for errors.
     */
    String errorPage;
    /**
     * Object containing the values of the previous form.
     */
    Object lastInput;
    /**
     * Errors from previous form validation.
     */
    List<ValidationError> validationErrors = new ArrayList<>();

    // Constr
    public ValidationLog() {
    }

    // Getters n Setters
    public String getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    public Object getLastInput() {
        return lastInput;
    }

    public void setLastInput( Object lastInput ) {
        this.lastInput = lastInput;
    }

    public boolean hasErrors() {
        return !this.getValidationErrors().isEmpty();
    }

    public List<ValidationError> getValidationErrors() {
        return this.validationErrors;
    }

    public ValidationError getErrorByInput( String inputName ) {
        for ( ValidationError error : this.getValidationErrors() ) {
            if ( error.getInputName().equals( inputName ) ) return error;
        }
        return null;
    }

    public void addValidationError( ValidationError error ) {
        this.getValidationErrors().add( error );
    }

    private void addErrorTo( String inputName, String error ) {
        ValidationError ve = getErrorByInput( inputName );
        if ( ve == null ) {
            ve = new ValidationError( inputName );
            this.addValidationError( ve );
        }
        ve.addError( error );
    }

    public void addError( SummerFormException sfe ) {
        this.addErrorTo( sfe.getFieldName(), sfe.getCustomMessage() );
    }
}
