package src.summer.beans.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationError {
    String inputName;
    List<String> errors = new ArrayList<>();

    // Constr
    public ValidationError( String inputName ) {
        this.inputName = inputName;
    }

    // Getters n Setters
    public String getInputName() {
        return inputName;
    }

    public void setInputName( String inputName ) {
        this.inputName = inputName;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors( List<String> errors ) {
        this.errors = errors;
    }

    public void addError( String error ) {
        errors.add( error );
    }
}
