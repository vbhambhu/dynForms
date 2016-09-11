package kennedy.ox.ac.uk.Models;


import kennedy.ox.ac.uk.Helpers.validation.TextValidation;
import kennedy.ox.ac.uk.Helpers.validation.Validation;

import java.util.*;

public class Field {


    private int fieldId;
    private String type;
    private String name;
    private String label;
    private Boolean isRequired;
    private String value;

    private Boolean hasError = false;
    private String errMsg;

    private String helpText = null;

    private List<Option> options = new ArrayList<>();

    private Validation validations = new TextValidation();



    public Field() {}


    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }


    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setHasErrorAndErrMsg(String errMsg, Object... args) {
        this.setHasError(true);
        this.errMsg = String.format(errMsg, args);
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public <T extends Validation> T getValidations()  {
        return (T)validations;
    }

    public void setValidations(Validation validations) {
        this.validations = validations;
    }


    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
}
