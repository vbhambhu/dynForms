package kennedy.ox.ac.uk.Models;


import java.util.ArrayList;
import java.util.List;

public class RestFieldValidation {


    private String field;

    private String message;

    public RestFieldValidation(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
