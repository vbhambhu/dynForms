package kennedy.ox.ac.uk.Models;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class RestValidationResult {


    private boolean hasError = false;
    private List<RestFieldValidation> errors = new ArrayList<>();

    public void addError(String field, String message) {
        RestFieldValidation error = new RestFieldValidation(field, message);
        errors.add(error);
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public List<RestFieldValidation> getErrors() {
        return errors;
    }

    public void setErrors(List<RestFieldValidation> errors) {
        this.errors = errors;
    }
}
